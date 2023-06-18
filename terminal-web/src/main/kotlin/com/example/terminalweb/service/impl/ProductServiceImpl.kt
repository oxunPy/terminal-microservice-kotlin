package com.example.terminalweb.service.impl

import com.example.common.constants.Currency
import com.example.common.forms.*
import com.example.common.forms.table.DataTablesForm
import com.example.common.forms.table.FilterForm
import com.example.common.forms.table.OrderForm
import com.example.common.util.DateUtils
import com.example.terminalweb.repository.ProductRepository
import com.example.common.dto.ExchangeRateDto
import com.example.common.dto.ProductDto
import com.example.common.dto.data_interface.ProductDtoProjection
import com.example.terminalweb.service.CurrencyService
import com.example.terminalweb.service.ProductService
import com.example.terminalweb.util.PoiUtil
import org.apache.commons.collections4.MapUtils
import org.apache.commons.lang.StringUtils
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.io.IOException
import java.io.InputStream
import java.math.BigDecimal
import java.sql.*
import java.text.NumberFormat
import java.util.*
import java.util.Date
import java.util.stream.Collectors
import javax.servlet.http.HttpServletResponse

@Service("ProductService")
class ProductServiceImpl(private val productRepository: ProductRepository, private val currencyService: CurrencyService): ProductService {

    @Value("\${download.template.csv}")
    private var CSV_FOLDER: String? = null

    @Value("\${spring.datasource.url}")
    private var dbUrl: String? = null

    @Value("\${spring.datasource.username}")
    private var dbUsername: String? = null

    @Value("\${spring.datasource.password}")
    private var dbPassword: String? = null

    override fun findAllProductDto(): List<ProductDto?>? {
        TODO("Not yet implemented")
    }

    override fun saveProductDto(dto: ProductDto?): ProductDto? {
        TODO("Not yet implemented")
    }

    override fun updateProductDto(dto: ProductDto?): ProductDto? {
        TODO("Not yet implemented")
    }

    override fun findProductDtoById(id: Long?): ProductDto? {
        TODO("Not yet implemented")
    }

    override fun getProductsDataTable(filterForm: FilterForm?): DataTablesForm<ProductForm?>? {
        val lastExchangeRateDto = currencyService.getLastExchangeRate()
        var sort = getSort(filterForm!!.orderMap()!!)
        if(sort.isEmpty)sort = Sort.by(Sort.Direction.ASC, "id")

        val pageRequest = PageRequest.of(filterForm.start / filterForm.length, filterForm.length, sort)
        val pageProducts = productRepository.pageProductsByName(StringUtils.defaultIfBlank(filterForm.searchText, ""), pageRequest)
        val productList = pageProducts!!.stream()
            .map { ProductForm(it!!.getId(), it.getProduct_name(), it.getRate(), getProductRate(it, lastExchangeRateDto!!), it.getCurrency(), it.getGroup_name(), it.getBarcode()) }.collect(Collectors.toList())

        return DataTablesForm(filterForm.draw, pageProducts.totalElements.toInt(), pageProducts.totalElements.toInt(), data = productList)
    }

    override fun getProductPriceDataTable(productId: Long?, filterForm: FilterForm?): DataTablesForm<ProductPriceForm?>? {
        val sort = Sort.by(Sort.Direction.ASC, "id")
        val pageRequest = PageRequest.of(0, Integer.MAX_VALUE, sort)

        var type: Int = 0
        val filterMap = filterForm!!.filter
        if(filterMap != null){
            if (filterMap.containsKey("buy") && MapUtils.getBoolean(filterMap, "buy")) type = 1
            if (filterMap.containsKey("sell") && MapUtils.getBoolean(filterMap, "sell")) type = 0
        }
        val pageProductPrice = productRepository.pageProductPrices(productId, type, pageRequest)
        val listPPForm = pageProductPrice?.content?.stream()?.map { ProductPriceForm(it!!.getId(), it.getDate(),
            NumberFormat.getCurrencyInstance().format(it.getRate()).substring(1) + " ${it.getCurrency()}", null, it.getCurrency()) }
            ?.collect(Collectors.toList())
        return DataTablesForm(filterForm.draw, pageProductPrice!!.totalElements.toInt(), pageProductPrice.totalElements.toInt(), data = listPPForm)
    }

    override fun getProductGroupDataTable(filterForm: FilterForm?): DataTablesForm<ProductGroupForm?>? {
        val pageRequest = PageRequest.of(filterForm!!.start / filterForm.length, filterForm.length, Sort.by(Sort.Direction.ASC, "id"))
        val pageProductGroup = productRepository.pageProductGroupByName(filterForm.searchText, pageRequest)
        val groupForms = pageProductGroup!!.stream().map { ProductGroupForm(it!!.getId(), it.getGroupName(), it.getInfo(), it.getParentId(), it.getParentName()) }.collect(Collectors.toList())
        return DataTablesForm(filterForm.draw, pageProductGroup.totalElements.toInt(), pageProductGroup.totalElements.toInt(), data = groupForms)
    }

    override fun getUnitDataTable(filterForm: FilterForm?): DataTablesForm<UnitForm?>? {
        val pageRequest = PageRequest.of(filterForm!!.start / filterForm.length, filterForm.length, Sort.by(Sort.Direction.ASC, "id"))
        val pageUnit = productRepository.pageUnit(pageRequest)
        val unitForms = pageUnit!!.stream().map { UnitForm(it!!.getId(), it.getName(), it.getSymbol()) }.collect(Collectors.toList())
        return DataTablesForm(filterForm.draw, pageUnit.totalElements.toInt(), pageUnit.totalElements.toInt(), data = unitForms)
    }

    override fun findAllProduct(): List<Select2Form?>? {
        return productRepository.getProductListByNameLike("", "", "", "")!!.stream()
            .map { Select2Form(it!!.getId(), it.getProduct_name()) }.collect(Collectors.toList())
    }

    override fun findAllProductGroup(): List<Select2Form?>? {
        return productRepository.pageProductGroupByName(null, PageRequest.of(0, Integer.MAX_VALUE))!!.stream()
            .map { Select2Form(it!!.getId(), it.getGroupName()) }.collect(Collectors.toList())
    }

    override fun findAllUnit(): List<Select2Form?>? {
        val pageUnit = productRepository.pageUnit(PageRequest.of(0, Integer.MAX_VALUE))
        return pageUnit!!.stream().map { Select2Form(it!!.getId(), it.getName()) }.collect(Collectors.toList())
    }

    override fun uploadExcelProducts(fileForm: FileForm?): Boolean? {
        var conn: Connection? = null
        try {
            //connection to the database
            conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword)
            conn.autoCommit = false
            val inStream: InputStream = fileForm!!.file!!.getInputStream()
            val workbook: Workbook = XSSFWorkbook(inStream)
            val unitSheet: Sheet = workbook.getSheetAt(0)
            val groupSheet: Sheet = workbook.getSheetAt(2)
            val productSheet: Sheet = workbook.getSheetAt(3)
            var count = 0
            val batchSize = 20

            //##################################################################
            // save unitSheet
            val sql1 = "INSERT INTO unit(created, status, server_id, name, symbol) VALUES(?, ?, ?, ?, ?)"
            val statement1 = conn.prepareStatement(sql1)
            val rowIteratorUnit: Iterator<Row> = unitSheet.rowIterator()
            rowIteratorUnit.next() // skip the header now
            while (rowIteratorUnit.hasNext()) {
                statement1.setTimestamp(1, Timestamp(System.currentTimeMillis()))
                statement1.setInt(2, 5)
                statement1.setInt(3, 0)
                val nextRow: Row = rowIteratorUnit.next()
                count++
                val cellIterator: Iterator<Cell> = nextRow.cellIterator()
                while (cellIterator.hasNext()) {
                    val nextCell: Cell = cellIterator.next()
                    val colIndex: Int = nextCell.getColumnIndex()
                    when (colIndex) {
                        0 -> {
                            val name: String = nextCell.getStringCellValue()
                            statement1.setString(4, name)
                        }

                        1 -> {
                            val symbol: String = nextCell.getStringCellValue()
                            statement1.setString(5, symbol)
                        }
                    }
                }
                if (nextRow.cellIterator().hasNext()) statement1.addBatch()
                if (count % batchSize == 0) {
                    statement1.executeBatch()
                }
            }
            // execute remaining queries
            statement1.executeBatch()
            conn.commit()

            //###################################################
            // save groupSheet
            val sql3 = "INSERT INTO product_group(created, status, server_id, name) VALUES(?, ?, ?, ?)"
            val statement3 = conn.prepareStatement(sql3)
            val rowIteratorGroup: Iterator<Row> = groupSheet.rowIterator()
            rowIteratorGroup.next() // skip the header now
            count = 0
            while (rowIteratorGroup.hasNext()) {
                statement3.setTimestamp(1, Timestamp(System.currentTimeMillis()))
                statement3.setInt(2, 5)
                statement3.setInt(3, 0)
                val nextRow: Row = rowIteratorGroup.next()
                count++
                val cellIterator: Iterator<Cell> = nextRow.cellIterator()
                while (cellIterator.hasNext()) {
                    val nextCell: Cell = cellIterator.next()
                    val colIndex: Int = nextCell.getColumnIndex()
                    when (colIndex) {
                        0 -> {
                            val name: String = nextCell.getStringCellValue()
                            statement3.setString(4, name)
                        }
                    }
                }
                if (nextRow.cellIterator().hasNext()) statement3.addBatch()
                if (count % batchSize == 0) {
                    statement3.executeBatch()
                }
            }
            // execute remaining queries
            statement3.executeBatch()
            conn.commit()

            //############################################################
            // save productSheet
            val sql4 =
                """
                INSERT INTO product(created, status, server_id, name, barcode, group_id, base_unit_id, alternate_unit_id, base_unit_val, alternate_unit_val)
                VALUES(?, ?, ?, ?, ?, (SELECT id FROM product_group where name = ? limit 1), (select id from unit where symbol = ? limit 1), (select id from unit where symbol = ? limit 1), ?, ?)
                """.trimIndent()
            //save purchase productRate
            val sql5 = """
                INSERT INTO product_rate(created, status, server_id, rate, currency_id, date, product_id, type)
                VALUES(?, ?, ?, ?, (SELECT id FROM currency WHERE code = ? limit 1), ?, ?, 1)
                """.trimIndent()

            //save sale productRate
            val sql6 = """
                INSERT INTO product_rate(created, status, server_id, rate, currency_id, date, product_id, type)
                VALUES(?, ?, ?, ?, (SELECT id FROM currency WHERE code = ? limit 1), ?, ?, 0)
                """.trimIndent()
            val statement4 = DriverManager.getConnection(dbUrl, dbUsername, dbPassword).prepareStatement(sql4)
            val statement5 = conn.prepareStatement(sql5)
            val statement6 = conn.prepareStatement(sql6)
            val rowIteratorProduct: Iterator<Row> = productSheet.iterator()
            rowIteratorProduct.next() // skip the header now
            count = 0
            while (rowIteratorProduct.hasNext()) {
                statement4.setTimestamp(1, Timestamp(System.currentTimeMillis()))
                statement4.setInt(2, 5)
                statement4.setInt(3, 0)
                statement5.setTimestamp(1, Timestamp(System.currentTimeMillis()))
                statement5.setInt(2, 5) //set status
                statement5.setInt(3, 0) //set server-id
                statement6.setTimestamp(1, Timestamp(System.currentTimeMillis()))
                statement6.setInt(2, 5) // set status
                statement6.setInt(3, 0) // set server-id
                val nextRow: Row = rowIteratorProduct.next()
                count++
                val cellIterator: Iterator<Cell> = nextRow.cellIterator()
                while (cellIterator.hasNext()) {
                    val nextCell: Cell = cellIterator.next()
                    val colIndex: Int = nextCell.getColumnIndex()
                    when (colIndex) {
                        0 -> {
                            val name: String = nextCell.getStringCellValue()
                            statement4.setString(4, name)
                        }

                        1 -> {
                            val barcode: String = PoiUtil.getStringValueFromCell(nextCell)
                            statement4.setString(5, barcode)
                        }

                        2 -> {
                            val group: String = nextCell.getStringCellValue()
                            statement4.setString(6, group)
                        }

                        3 -> {
                            val baseUnitSymbol: String = nextCell.getStringCellValue()
                            statement4.setString(7, baseUnitSymbol)
                        }

                        4 -> {
                            val alternateUnitSymbol: String = nextCell.getStringCellValue()
                            statement4.setString(8, alternateUnitSymbol)
                        }

                        5 -> {
                            val baseUnitVal: Double = nextCell.getNumericCellValue()
                            statement4.setDouble(9, baseUnitVal)
                        }

                        6 -> {
                            val alternateUnitVal: Double = nextCell.getNumericCellValue()
                            statement4.setDouble(10, alternateUnitVal)
                        }

                        7 -> {
                            val purchasePrice: Double = nextCell.getNumericCellValue()
                            statement5.setDouble(4, purchasePrice)
                        }

                        8 -> {
                            val pCurrency: String = nextCell.getStringCellValue()
                            statement5.setString(5, pCurrency)
                        }

                        9 -> {
                            val salesPrice: Double = nextCell.getNumericCellValue()
                            statement6.setDouble(4, salesPrice)
                        }

                        10 -> {
                            val sCurrency: String = nextCell.getStringCellValue()
                            statement6.setString(5, sCurrency)
                        }

                        11 -> {
                            val dateStr: String = nextCell.getStringCellValue()
                            statement5.setTimestamp(6, Timestamp(DateUtils.parse(dateStr, "dd.MM.yyyy")!!.getTime()))
                            statement6.setTimestamp(6, Timestamp(DateUtils.parse(dateStr, "dd.MM.yyyy")!!.getTime()))
                        }
                    }
                }
                if (nextRow.cellIterator().hasNext()) {
                    statement4.execute()
                    statement5.setLong(7, productRepository.lastInsertProductId()!!)
                    statement6.setLong(7, productRepository.lastInsertProductId()!!)
                    statement5.addBatch()
                    statement6.addBatch()
                }
                if (count % batchSize == 0) {
                    statement5.executeBatch()
                    statement6.executeBatch()
                }
            }
            // execute remaining queries
            statement5.executeBatch()
            statement6.executeBatch()
            conn.commit()
            conn.close()
        } catch (e: SQLException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return true
    }

    override fun getProductExcelTemplate(response: HttpServletResponse?) {
        TODO("Not yet implemented")
    }

    override fun getProductsExcel(locale: Locale?, searchText: String?, response: HttpServletResponse?) {
        TODO("Not yet implemented")
    }

    override fun getUnit(unitId: Long?): UnitForm? {
        TODO("Not yet implemented")
    }

    override fun getProductGroup(productGroupId: Long?): ProductGroupForm? {
        TODO("Not yet implemented")
    }

    override fun createAndEditUnit(unit: UnitForm?): Boolean? {
        TODO("Not yet implemented")
    }

    override fun createAndEditProductGroup(productGroup: ProductGroupForm?): Boolean? {
        TODO("Not yet implemented")
    }

    override fun deleteUnit(unitId: Long?): Boolean? {
        return if(unitId == null) false else productRepository.deleteUnit(unitId)
    }

    override fun deleteProductGroup(productGroupId: Long?): Boolean? {
        return if(productGroupId == null) false else productRepository.deleteProductGroup(productGroupId)
    }

    override fun saveProduct(productSaveOrEditForm: ProductSaveOrEditForm?): Boolean? {
        TODO("Not yet implemented")
    }

    override fun getProduct(productId: Long?): ProductSaveOrEditForm? {
        TODO("Not yet implemented")
    }

    override fun deleteProduct(productId: Long?): Boolean? {
        TODO("Not yet implemented")
    }


    private fun getProductRate(searchedProduct: ProductDtoProjection, lastExchangeRate: ExchangeRateDto): BigDecimal? {
        if(searchedProduct.getCurrency() == null || searchedProduct.getCurrency().equals(Currency.USD.name) && lastExchangeRate.mainCurrency != null &&
                                                                            lastExchangeRate.mainCurrency.equals(Currency.USD.name)){
            return getRateUZS(lastExchangeRate, (if(searchedProduct.getRate() != null) searchedProduct.getRate() else BigDecimal.ZERO))
        }
        return searchedProduct.getRate()
    }

    private fun getRateUZS(exchangeRateDto: ExchangeRateDto, productRate: BigDecimal?): BigDecimal? {
        return if (!exchangeRateDto.mainCurrency.equals(Currency.UZS.name)) {
            productRate!!.multiply(exchangeRateDto.toCurrencyVal)
                .divide(exchangeRateDto.mainCurrencyVal)
        } else productRate
    }

    private fun getSort(orderMap: Map<String, OrderForm>): Sort {
        val orders: ArrayList<Sort.Order> = ArrayList()
        orderMap.forEach{(column, orderForm) ->
            var property: String? = null
            when(column){
                "name" -> property = "name"
                "barcode" -> property = "barcode"
            }
            val direction = Sort.Direction.fromString(orderForm.dir!!)
            orders.add(if(direction.isAscending) Sort.Order.asc(property!!) else Sort.Order.desc(property!!))
        }
        return Sort.by(orders)
    }
}