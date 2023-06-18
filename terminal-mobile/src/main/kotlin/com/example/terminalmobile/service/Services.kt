package com.example.terminalmobile.service

import com.example.common.dto.*
import java.math.BigDecimal
import java.time.LocalDate

interface AccountService{
    fun getAllAccountsWithType(type: Int): List<AccountDto?>?

    fun getAllAccountsWithTypeAndNameLike(type: Int, name: String?): List<AccountDto?>?
}

interface DealerService{
    fun getDealersWithName(name: String?): List<DealerDto?>?
}

interface InvoiceService{
    fun getInvoiceUid(uid: String?): InvoiceDto?

    fun create(source: Int, warehouseId: Int, invoiceDto: InvoiceDto?, uid: String?): Long?

    fun update(source: Int, warehouseId: Int, invoiceDto: InvoiceDto?, uid: String?): Long?

    fun getAll(type: Int, warehouseId: Int, source: Int, page: Int, size: Int, fromDate: LocalDate?, toDate: LocalDate?): PagedResponseDto<InvoiceDto?>?

    fun getAllItemsById(id: Long?): List<InvoiceItemDto?>?
}

interface ProductService{
    fun findProductListByBarcode(barcode: String?): List<ProductDto?>?

    fun findProductByNameLike(productName: String?): List<ProductDto?>?

    fun getProductOstatokById(productId: Int?, warehouseId: Int?): BigDecimal?
}

interface ReportService{
    fun getProductsByDate(fromDate: String?,toDate: String?,itemsPerPage: Int, numberOfPage: Int): List<ReportByDateDto?>?

    fun getTodaysReport(source: Int, warehouseId: Int): ReportTodayIncomeExpenseDto?
}

interface UserService{
    fun login(login: String?, password: String?): UserDto?
}

interface WareHouseService{
    fun getAllWareHousesWithName(name: String?): List<WareHouseDto?>?
}

interface CurrencyService {
    fun getLastExchangeRate(): ExchangeRateDto?
}