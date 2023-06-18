package com.example.terminalweb.service.impl

import com.example.common.constants.*
import com.example.common.constants.Currency
import com.example.common.forms.InvoiceForm
import com.example.common.forms.InvoiceItemForm
import com.example.common.forms.table.DataTablesForm
import com.example.common.forms.table.FilterForm
import com.example.common.util.DateUtils
import com.example.terminalweb.repository.InvoiceRepository
import com.example.common.dto.ExchangeRateDto
import com.example.common.dto.InvoiceDto
import com.example.common.dto.InvoiceItemDto
import com.example.common.dto.PagedResponseDto
import com.example.common.dto.data_interface.InvoiceDtoProjection
import com.example.common.dto.data_interface.InvoiceItemDtoProjection
import com.example.common.dto.data_interface.PagedResponseDtoProjection
import com.example.terminalweb.service.CurrencyService
import com.example.terminalweb.service.InvoiceService
import org.apache.commons.collections4.MapUtils
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.util.*
import java.util.function.Consumer
import java.util.stream.Collectors


@Service("InvoiceService")
class InvoiceServiceImpl(private val invoiceRepository: InvoiceRepository, private val currencyService: CurrencyService): InvoiceService {
    override fun getInvoiceDatatable(locale: Locale?, filter: FilterForm?): DataTablesForm<InvoiceForm?>? {
        var fromDate: Date? = null
        var toDate: Date? = null
        var types: String? = null
        var warehouseId: Int? = null

        val filterMap: Map<String, Any?> = filter!!.filter!!
        fromDate = DateUtils.parse(MapUtils.getString<String>(filterMap, "fromDate"), DateUtils.PATTERN_2)
        toDate = DateUtils.parse(MapUtils.getString<String>(filterMap, "toDate"), DateUtils.PATTERN_2)
        types = MapUtils.getString(filterMap, "typeOrdinal")
        warehouseId = MapUtils.getInteger(filterMap, "warehouseId")

        val invoiceList = invoiceRepository.getInvoices(types, warehouseId, null, filter.length, filter.start, DateUtils.toLocale(fromDate), DateUtils.toLocale(toDate), Status.ACTIVE.ordinal, Status.UPDATED.ordinal, Status.TO_PAY.ordinal)

        val formList = invoiceList!!.stream().map<InvoiceForm> { i: InvoiceDtoProjection? ->
            InvoiceForm(
                i!!.getId(),
                i.getAccount_id(),
                i.getAccount_printable_name(),
                i.getWarehouseId(),
                i.getWarehouse(),
                DateUtils.parse(
                    i.getDate(), DateUtils.PATTERN_1
                ),
                InvoiceType.values()[i.getType()!!].getLocaleValue(locale!!),
                i.getInfo(),
                i.getCount_of_items(),
                i.getTotal()
            )
        }.collect(Collectors.toList<InvoiceForm>())

        val dataTable :DataTablesForm<InvoiceForm?> = DataTablesForm()
        dataTable.recordsFiltered = invoiceList.size
        dataTable.recordsTotal = invoiceList.size
        dataTable.data = formList
        return dataTable
    }

    override fun getInvoiceItemsDatatable(invoiceId: Long?): DataTablesForm<InvoiceItemForm?>? {
        TODO("Not yet implemented")
    }

}