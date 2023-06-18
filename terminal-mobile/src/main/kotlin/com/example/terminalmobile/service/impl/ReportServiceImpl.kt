package com.example.terminalmobile.service.impl

import com.example.common.constants.TypeSource
import com.example.common.dto.ReportByDateDto
import com.example.common.dto.ReportTodayIncomeExpenseDto
import com.example.common.util.DateUtils
import com.example.terminalmobile.repository.InvoiceRepository
import com.example.terminalmobile.repository.ReportRepository
import com.example.terminalmobile.service.ReportService
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.stream.Collectors

@Service("ReportService")
class ReportServiceImpl(private val reportRepository: ReportRepository, private val invoiceRepository: InvoiceRepository): ReportService {
    override fun getProductsByDate(fromDate: String?, toDate: String?, itemsPerPage: Int, numberOfPage: Int): List<ReportByDateDto?>? {
        var from: String? = null
        var to: String? = null
        if (fromDate!!.isEmpty()) from = LocalDate.of(1970, 1, 1).toString()
        if (toDate!!.isEmpty()) to = LocalDate.parse(LocalDate.now().toString(), DateTimeFormatter.ofPattern("yyy-MM-dd")).toString()

        return reportRepository.getInvoiceItemByDate(DateUtils.stringToLocalDateFormat(from), DateUtils.stringToLocalDateFormat(to), itemsPerPage,(numberOfPage - 1) * itemsPerPage)?.stream()
            ?.map { ReportByDateDto(it!!.getInvoice_item_id(), it.getDate_of_sell(), it.getProduct_quantity(), it.getProduct_price(), it.getType_of_operation(), it.getProduct_name(), it.getPrice_currency()) }
            ?.collect(Collectors.toList())
    }

    override fun getTodaysReport(source: Int, warehouseId: Int): ReportTodayIncomeExpenseDto? {
        val sourceType: String = if (source == 0) TypeSource.DESKTOP.name else TypeSource.MOBILE.name
        val todayIncomeExpense = reportRepository.getTodayIncomeExpense(LocalDate.now(), sourceType, warehouseId, 1, -1)
        return ReportTodayIncomeExpenseDto(todayIncomeExpense!!.get().getIncome(), todayIncomeExpense.get().getExpense())
    }
}