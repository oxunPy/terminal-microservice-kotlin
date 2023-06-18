package com.example.terminalmobile.repository

import com.example.common.dto.data_interface.*
import com.example.terminalmobile.entity.RootEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*

interface AccountRepository: JpaRepository<RootEntity, Long>{
    @Query(nativeQuery = true, value = "select * from get_accounts_with_type(:type, :active, :updated)")
    fun getAllAccountsWithType(
        @Param("type") type: Int,
        @Param("active") active: Int,
        @Param("updated") updated: Int
    ): List<AccountDtoProjection?>?

    @Query(
        nativeQuery = true,
        value = "select * from get_accounts_with_type_and_name_like(:type, :name, :active, :updated)"
    )
    fun getAllAccountsWithTypeAndNameLike(
        @Param("type") type: Int,
        @Param("name") name: String?,
        @Param("active") active: Int,
        @Param("updated") updated: Int
    ): List<AccountDtoProjection?>?
}

interface DealerRepository: JpaRepository<RootEntity, Long>{
    @Query(
        nativeQuery = true, value = "select " +
                "id as id, " +
                "name as name, " +
                "dealer_code as dealerCode " +
                " from get_dealers_with_name_like(:name)"
    )
    fun getDealersWithNameLike(@Param("name") name: String?): List<DealerDtoProjection?>?
}

interface InvoiceRepository: JpaRepository<RootEntity, Long>{
    @Query(
        nativeQuery = true,
        value = "select create_invoice(:account_id, :date, :type, :info, :type_source, :status, :uid)"
    )
    fun createInvoice(
        @Param("account_id") accountId: Long?,
        @Param("date") date: LocalDate?,
        @Param("type") type: Int,
        @Param("info") info: String?,
        @Param("type_source") typeSource: String?,
        @Param("status") status: Int,
        @Param("uid") uid: String?
    ): Long?

    @Query(
        nativeQuery = true,
        value = "select create_invoice_item_amount(:invoice_item_id, :amount, :rate, :original_rate, :currency_id, :convertion, :denominator, :type, :status)"
    )
    fun createInvoiceItemAmount(
        @Param("invoice_item_id") invoiceItemId: Long?,
        @Param("amount") amount: BigDecimal?,
        @Param("rate") sellingRate: BigDecimal?,
        @Param("original_rate") originalRate: BigDecimal?,
        @Param("currency_id") currencyId: Int,
        @Param("convertion") conversion: BigDecimal?,
        @Param("denominator") denominator: BigDecimal?,
        @Param("type") type: Int,
        @Param("status") status: Int
    ): Long?

    @Query(
        nativeQuery = true,
        value = "select create_invoice_item(:invoice_id, :product_id, :qty, :action_type, :status)"
    )
    fun createInvoiceItem(
        @Param("invoice_id") invoiceId: Long?,
        @Param("product_id") productId: Long?,
        @Param("qty") quantity: BigDecimal?,
        @Param("action_type") actionType: Int,
        @Param("status") status: Int
    ): Long?

    @Query(
        nativeQuery = true,
        value = "select * from get_invoices_count_and_sum(:types, :type_source, :from_date, :to_date, :active, :updated, :to_pay)"
    )
    fun getInvoicesCountAndSum(
        @Param("types") types: String?,
        @Param("type_source") typeSource: String?,
        @Param("from_date") fromDate: LocalDate?,
        @Param("to_date") toDate: LocalDate?,
        @Param("active") active: Int,
        @Param("updated") updated: Int,
        @Param("to_pay") toPay: Int
    ): PagedResponseDtoProjection?

    @Query(
        nativeQuery = true,
        value = "select * from get_invoice_items_by_invoice_id(:invoice_id, :active, :updated, :to_pay)"
    )
    fun getInvoiceItemsByInvoiceId(
        @Param("invoice_id") invoiceId: Long?,
        @Param("active") active: Int,
        @Param("updated") updated: Int,
        @Param("to_pay") toPay: Int
    ): List<InvoiceItemDtoProjection?>?

    @Query(nativeQuery = true, value = "select * from get_invoice_by_uid(:uid)")
    fun getInvoiceByUid(@Param("uid") uid: String?): List<InvoiceDtoProjection?>?

    @Query(
        nativeQuery = true,
        value = "select update_invoice(:dealer_client_id, :warehouse_id, :type, :info, :type_source, :status, :invoice_id, :date)"
    )
    fun updateInvoice(
        @Param("dealer_client_id") dealer_client_id: Int,
        @Param("warehouse_id") warehouseId: Int,
        @Param("type") type: Int,
        @Param("info") info: String?,
        @Param("type_source") typeSource: String?,
        @Param("status") status: Int,
        @Param("invoice_id") invoiceId: Int?,
        @Param("date") date: LocalDate?
    ): Long?

    @Query(nativeQuery = true, value = "select cast(change_invoice_item_status(:invoice_id, :status) as text)")
    fun changeInvoiceItemStatus(@Param("invoice_id") invoiceId: Long?, @Param("status") status: Int): List<String?>?

    @Query(
        nativeQuery = true,
        value = "select cast(change_invoice_item_amount_status(:invoice_item_id, :status) as text)"
    )
    fun changeInvoiceItemAmountStatus(
        @Param("invoice_item_id") invoiceItemId: Long?,
        @Param("status") status: Int
    ): List<String?>?

    @Query(
        nativeQuery = true,
        value = "select * from get_invoices_go_down(:type_source, :lim, :offs, :from_date, :to_date, :warehous_id)"
    )
    fun getInvoicesGoDown(
        @Param("type_source") typeSource: String?,
        @Param("warehous_id") warehouseId: Int?,
        @Param("lim") limit: Int,
        @Param("offs") offset: Int,
        @Param("from_date") fromDate: LocalDate?,
        @Param("to_date") toDate: LocalDate?
    ): List<InvoiceDtoProjection?>?

    @Query(
        nativeQuery = true,
        value = "select * from get_invoices(:types, :type_source, :lim, :offs, :from_date, :to_date, :active, :updated, :to_pay, :warehous_id, null)"
    )
    fun getInvoices(
        @Param("types") types: String?,
        @Param("warehous_id") warehouseId: Int?,
        @Param("type_source") typeSource: String?,
        @Param("lim") limit: Int,
        @Param("offs") offset: Int,
        @Param("from_date") fromDate: LocalDate?,
        @Param("to_date") toDate: LocalDate?,
        @Param("active") active: Int,
        @Param("updated") updated: Int,
        @Param("to_pay") toPay: Int
    ): List<InvoiceDtoProjection?>?
}

interface ProductRepository: JpaRepository<RootEntity, Long>{
    @Query(nativeQuery = true, value = "select * from get_product_by_barcode(:barcode)")
    fun getProductListByBarcode(@Param("barcode") barcode: String?): List<ProductDtoProjection?>?

    @Query(nativeQuery = true, value = " select * from getproductostatok(:id, :warehouse_id, :date)")
    fun getProductOstatokByProductId(
        @Param("id") productId: Int,
        @Param("warehouse_id") warehouseId: Int,
        @Param("date") dateParam: Date?
    ): BigDecimal?

    @Query(
        nativeQuery = true,
        value = "select * from get_product_by_barcode(:barcode)",
        countProjection = "select count(*) from get_product_by_barcode(:barcode)"
    )
    fun pageProductsByBarcode(@Param("barcode") barcode: String?, pageable: Pageable?): Page<ProductDtoProjection?>?

    @Query(nativeQuery = true, value = "select * from get_product_by_name(:param1, :param2, :param3, :param4)")
    fun getProductListByNameLike(
        @Param("param1") param1: String?,
        @Param("param2") param2: String?,
        @Param("param3") param3: String?,
        @Param("param4") param4: String?
    ): List<ProductDtoProjection?>?
}

interface ReportRepository: JpaRepository<RootEntity, Long>{
    @Query(
        nativeQuery = true,
        value = "SELECT * FROM get_invoice_item_by_date(:from_date, :to_date, :items_per_page, :number_of_page)"
    )
    fun getInvoiceItemByDate(
        @Param("from_date") fromDate: LocalDate?,
        @Param("to_date") toDate: LocalDate?,
        @Param("items_per_page") size: Int,
        @Param("number_of_page") page: Int
    ): List<ReportByDateProjection?>?

    @Query(
        nativeQuery = true,
        value = "SELECT * FROM get_today_income_and_expense(:req_date, :type_source, :req_income_type, :req_expense_type, :warehouse_id)"
    )
    fun getTodayIncomeExpense(
        @Param("req_date") reqDate: LocalDate?,
        @Param("type_source") typeSource: String?,
        @Param("warehouse_id") warehouseId: Int,
        @Param("req_income_type") incomeType: Int,
        @Param("req_expense_type") expenseType: Int
    ): Optional<ReportTodayIncomeExpenseProjection>?
}

interface UserRepository: JpaRepository<RootEntity, Long>{
    @Query(nativeQuery = true, value = "select * from get_user(:login, :password)")
    fun getUser(@Param("login") login: String?, @Param("password") password: String?): Optional<UserDtoProjection?>?

    @Query(nativeQuery = true, value = "select * from get_user_by_login(:login)")
    fun findByLogin(@Param("login") login: String?): Optional<UserDtoProjection?>?
}

interface WareHouseRepository: JpaRepository<RootEntity, Long>{
    @Query(nativeQuery = true, value = "select * from get_warehouses_by_name(:name, 0, 10000000)")
    fun getAllWareHousesWithName(@Param("name") name: String?): List<WareHouseDtoProjection?>?
}

interface CurrencyRepository: JpaRepository<RootEntity, Long>{
    @Query(nativeQuery = true, value = "select * from get_last_exchange_rate()")
    fun getLastExchangeRate(): Optional<ExchangeRateDtoProjection?>?
}