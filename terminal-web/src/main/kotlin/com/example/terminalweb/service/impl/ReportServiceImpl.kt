package com.example.terminalweb.service.impl

import com.example.common.constants.InvoiceType
import com.example.common.constants.Status
import com.example.common.constants.TypeSource
import com.example.common.forms.InvoiceForm
import com.example.common.forms.ReportByDateForm
import com.example.common.forms.table.DataTablesForm
import com.example.common.forms.table.FilterForm
import com.example.common.forms.table.OrderForm
import com.example.common.util.DateUtils
import com.example.terminalweb.repository.InvoiceRepository
import com.example.terminalweb.repository.ReportRepository
import com.example.common.dto.ReportByDateDto
import com.example.common.dto.ReportTodayIncomeExpenseDto
import com.example.terminalweb.service.ReportService
import org.apache.commons.collections4.MapUtils
import org.apache.commons.lang3.StringUtils
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.stream.Collectors

@Service("ReportService")
class ReportServiceImpl(private val reportRepository: ReportRepository, private val invoiceRepository: InvoiceRepository): ReportService {
    override fun getReportsDatatable(filterForm: FilterForm?): DataTablesForm<ReportByDateForm?>? {
        var sort = getSort(filterForm!!.orderMap())

        if(sort.isEmpty){
            sort = Sort.by(Sort.Order(Sort.Direction.ASC, "invoice_item_id"))
        }

        var from: String? = null
        var to: String? = null

        val filterMap = filterForm.filter

        if(filterMap != null){
            from = StringUtils.defaultIfEmpty(MapUtils.getString(filterMap, "from"), "1970-01-01")
            to = StringUtils.defaultIfEmpty(MapUtils.getString(filterMap, "to"), LocalDate.parse(LocalDate.now().toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd")).toString())
        }

        val pageRequest = PageRequest.of(filterForm.start / filterForm.length, filterForm.length, sort)
        val pageReports = reportRepository.getInvoiceItemByDate(DateUtils.toLocale(DateUtils.parse(from, DateUtils.PATTERN_2)), DateUtils.toLocale(DateUtils.parse(to, DateUtils.PATTERN_2)), Integer.MAX_VALUE, 0, pageRequest)

        val reportList = pageReports!!.stream()
            .map { ReportByDateForm(it!!.getInvoice_item_id(), DateUtils.format(DateUtils.fromLocale(it.getDate_of_sell()), "yyyy-MM-dd"), it.getProduct_quantity(), it.getProduct_price(), it.getType_of_operation(), it.getProduct_name(), it.getPrice_currency()) }
            .collect(Collectors.toList())

        val dataTable: DataTablesForm<ReportByDateForm?> = DataTablesForm()
        dataTable.data = reportList
        dataTable.draw = filterForm.draw
        dataTable.recordsFiltered = pageReports.totalElements.toInt()
        dataTable.recordsTotal = pageReports.totalElements.toInt()
        return dataTable
    }

    override fun getReportSales(locale: Locale?, filterForm: FilterForm?): DataTablesForm<InvoiceForm?>? {
        return getInvoiceByType(locale!!, filterForm!!, InvoiceType.RASXOD_KLIENT)
    }

    override fun getReportPurchases(locale: Locale?, filterForm: FilterForm?): DataTablesForm<InvoiceForm?>? {
        return getInvoiceByType(locale!!, filterForm!!, InvoiceType.PRIXOD_BAZA)
    }

    override fun getReportReturnClient(locale: Locale?, filterForm: FilterForm?): DataTablesForm<InvoiceForm?>? {
        return getInvoiceByType(locale!!, filterForm!!, InvoiceType.VOZVRAT_KLIENT)
    }

    override fun getReportReturnBase(locale: Locale?, filterForm: FilterForm?): DataTablesForm<InvoiceForm?>? {
        return getInvoiceByType(locale!!, filterForm!!, InvoiceType.VOZVRAT_BAZA)
    }

    private fun getSort(orderMap: Map<String, OrderForm>?): Sort {
        val orders: ArrayList<Sort.Order> = ArrayList()

        orderMap?.forEach{(column, orderForm) ->
            var property: String? = null
            when(column){
                "productName" -> property = "product_name"
                "typeOfOperation" -> property = "type_of_operation"
                "dateOfSell" -> property = "date_of_sell"
            }
            val direction = Sort.Direction.fromString(orderForm.dir!!)
            orders.add(if(direction.isAscending) Sort.Order.asc(property!!) else Sort.Order.desc(property!!))
        }
        return Sort.by(orders)
    }

    private fun getInvoiceByType(locale: Locale, filter: FilterForm, invoiceType: InvoiceType): DataTablesForm<InvoiceForm?>{
        var sort = getSort(filter.orderMap())

        if(sort.isEmpty){
            sort = Sort.by(Sort.Direction.ASC, "id")
        }

        var from: String? = null
        var to: String? = null
        var clientId: Long? = null
        var wareHouseId: Long? = null

        val filterMap = filter.filter
        if(filterMap != null){
            from = StringUtils.defaultIfEmpty(MapUtils.getString(filterMap, "from"), "1970-01-01")
            to = StringUtils.defaultIfEmpty(MapUtils.getString(filterMap, "to"), LocalDate.parse(LocalDate.now().toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd")).toString())
            clientId = MapUtils.getLong(filterMap, "dealerClientId")
            wareHouseId = MapUtils.getLong(filterMap, "warehouseId")
        }

        val pageRequest = PageRequest.of(filter.start / filter.length, filter.length, sort)

        val pageInvoices = invoiceRepository.getInvoicesPageable(invoiceType.ordinal.toString(),
                                             DateUtils.toLocale(DateUtils.parse(from, DateUtils.PATTERN_2)), DateUtils.toLocale(DateUtils.parse(to, DateUtils.PATTERN_2)),
                                             Status.ACTIVE.ordinal, Status.UPDATED.ordinal, Status.TO_PAY.ordinal, clientId, wareHouseId, pageRequest)
        val invoiceFormList = pageInvoices!!.stream().map { InvoiceForm(it!!.getId(), it.getAccount_id(), it.getAccount_printable_name(), it.getWarehouseId(),
                                                                        it.getWarehouse(), DateUtils.parse(it.getDate(), "yyyy-MM-dd"),
                                                                        InvoiceType.values()[it.getType()!!].getLocaleValue(locale), it.getInfo(), it.getCount_of_items(), it.getTotal()) }.collect(Collectors.toList())

        val dataTable: DataTablesForm<InvoiceForm?> = DataTablesForm()
        dataTable.data = invoiceFormList
        dataTable.recordsTotal = pageInvoices.totalElements.toInt()
        dataTable.recordsFiltered = pageInvoices.totalElements.toInt()
        dataTable.draw = pageInvoices.totalElements.toInt()
        return dataTable
    }
}