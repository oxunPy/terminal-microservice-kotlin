package com.example.terminalweb.service

import com.example.common.constants.AccountType
import com.example.common.dto.*
import com.example.common.forms.*
import com.example.common.forms.table.DataTablesForm
import com.example.common.forms.table.FilterForm
import org.springframework.web.multipart.MultipartFile
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

interface AccountService {
    fun getAllAccountsWithType(type: Int): List<AccountDto?>?

    fun getAllAccountsWithTypeAndNameLike(type: Int, name: String?): List<AccountDto?>?

    fun findAllDealerClients(accountType: AccountType?): List<Select2Form?>?

    fun getAccountDataTable(filterForm: FilterForm?): DataTablesForm<AccountForm?>?
}

interface CurrencyService {
    fun getLastExchangeRate(): ExchangeRateDto?
}

interface DealerService {
    fun getDealersDataTable(filterForm: FilterForm?): DataTablesForm<DealerForm?>?

    fun findAllDealer(): List<Select2Form?>?
}

interface InvoiceService{
    fun getInvoiceDatatable(locale: Locale?, filterForm: FilterForm?): DataTablesForm<InvoiceForm?>?

    fun getInvoiceItemsDatatable(invoiceId: Long?): DataTablesForm<InvoiceItemForm?>?
}

interface ProductService{
    fun findAllProductDto(): List<ProductDto?>?

    fun saveProductDto(dto: ProductDto?): ProductDto?

    fun updateProductDto(dto: ProductDto?): ProductDto?

    fun findProductDtoById(id: Long?): ProductDto?

    fun getProductsDataTable(filterForm: FilterForm?): DataTablesForm<ProductForm?>?

    fun getProductPriceDataTable(productId: Long?, filterForm: FilterForm?): DataTablesForm<ProductPriceForm?>?

    fun getProductGroupDataTable(filterForm: FilterForm?): DataTablesForm<ProductGroupForm?>?

    fun getUnitDataTable(filterForm: FilterForm?): DataTablesForm<UnitForm?>?

    fun findAllProduct(): List<Select2Form?>?

    fun findAllProductGroup(): List<Select2Form?>?

    fun findAllUnit(): List<Select2Form?>?

    fun uploadExcelProducts(fileForm: FileForm?): Boolean?

    fun getProductExcelTemplate(response: HttpServletResponse?)

    fun getProductsExcel(locale: Locale?, searchText: String?, response: HttpServletResponse?)

    fun getUnit(unitId: Long?): UnitForm?

    fun getProductGroup(productGroupId: Long?): ProductGroupForm?

    fun createAndEditUnit(unit: UnitForm?): Boolean?

    fun createAndEditProductGroup(productGroup: ProductGroupForm?): Boolean?

    fun deleteUnit(unitId: Long?): Boolean?

    fun deleteProductGroup(productGroupId: Long?): Boolean?

    fun saveProduct(productSaveOrEditForm: ProductSaveOrEditForm?): Boolean?

    fun getProduct(productId: Long?): ProductSaveOrEditForm?

    fun deleteProduct(productId: Long?): Boolean?
}

interface QRCodeService{
    fun generateQRCode(qrContent: String?, width: Int, height: Int): ByteArray?
}

interface ReportService{

    fun getReportsDatatable(filterForm: FilterForm?): DataTablesForm<ReportByDateForm?>?

    fun getReportSales(locale: Locale?, filterForm: FilterForm?): DataTablesForm<InvoiceForm?>?

    fun getReportPurchases(locale: Locale?, filterForm: FilterForm?): DataTablesForm<InvoiceForm?>?

    fun getReportReturnClient(locale: Locale?, filterForm: FilterForm?): DataTablesForm<InvoiceForm?>?

    fun getReportReturnBase(locale: Locale?, filterForm: FilterForm?): DataTablesForm<InvoiceForm?>?
}

interface SettingService{
    fun getCompanyDataTable(filterForm: FilterForm?, request: HttpServletRequest?): DataTablesForm<CompanyForm?>?

    fun saveCompany(companyForm: CompanyForm?): Boolean?

    fun editCompany(companyForm: CompanyForm?): Boolean?

    fun deleteCompany(companyId: Long?): Boolean?

    fun saveCompanyLogoAndGetURI(request: HttpServletRequest?, companyId: Long?, file: MultipartFile?): String?

    fun saveCompanyFaviconAndGetURI(request: HttpServletRequest?, companyId: Long?, file: MultipartFile?): String?

    fun findAllCompany(): List<Select2Form?>?

    fun findAllCurrency(): List<Select2Form?>?

    fun getCompanyById(request: HttpServletRequest?, id: Long?): CompanyForm?

    fun getMainCompanyLogoName(): String?

    fun getMainCompanyFaviconName(): String?
}

interface WareHouseService{
    fun getAllWareHousesWithName(name: String?): List<WareHouseDto?>?

    fun getWareHouseDataTable(filterForm: FilterForm?): DataTablesForm<WareHouseForm?>?

    fun findAllWareHouse(): List<WareHouseForm?>?

    fun deleteWarehouse(warehouseId: Long?): Boolean?

    fun getWarehouse(warehouseId: Long?): WareHouseForm?

    fun saveWarehouse(wareHouseForm: WareHouseForm?): Boolean?

    fun editWarehouse(wareHouseForm: WareHouseForm?): Boolean?
}