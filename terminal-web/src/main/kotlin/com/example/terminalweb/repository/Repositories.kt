package com.example.terminalweb.repository

import com.example.common.dto.data_interface.*
import com.example.terminalweb.entity.RootEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.util.*

@Repository
interface AccountRepository : JpaRepository<RootEntity, Long> {
    @Query(nativeQuery = true, value = "select * from get_accounts_with_type(:type, :active, :updated)")
    fun getAllAccountsWithType(
        @Param("type") type: Int,
        @Param("active") active: Int,
        @Param("updated") updated: Int
    ): List<AccountDtoProjection?>?

    @Query(
        nativeQuery = true,
        value = "select * from get_accounts_with_type(:type, :active, :updated)",
        countProjection = "select count(*) from get_accounts_with_type(:type, :active, :updated)"
    )
    fun pageAllAccountsWithType(
        @Param("type") type: Int,
        @Param("active") active: Int,
        @Param("updated") updated: Int, pageable: Pageable?
    ): Page<AccountDtoProjection?>?
}

@Repository
interface CurrencyRepository : JpaRepository<RootEntity, Long> {
    @Query(nativeQuery = true, value = "select * from get_last_exchange_rate()")
    fun getLastExchangeRate(): Optional<ExchangeRateDtoProjection?>?
}

@Repository
interface DealerRepository : JpaRepository<RootEntity, Long> {
    @Query(
        nativeQuery = true, value = "select " +
                "id as id, " +
                "name as name, " +
                "dealer_code as dealerCode " +
                " from get_dealers_with_name_like(:name)"
    )
    fun getDealersWithNameLike(@Param("name") name: String?): List<DealerDtoProjection?>?

    @Query(
        nativeQuery = true,
        value = "select " +
                "id as id, " +
                "name as name, " +
                "dealer_code as dealerCode " +
                "from get_dealers_with_name_like(:name)",
        countProjection = "select count(*) from get_dealers_with_name_like(:name)"
    )
    fun pageDealers(name: String?, pageable: Pageable?): Page<DealerDtoProjection?>?

    @Query(nativeQuery = true, value = "select count(*) from get_dealers_with_name_like(:name)")
    fun count(@Param("name") name: String?): Int?
}


@Repository
interface InvoiceRepository : JpaRepository<RootEntity, Long> {
    @Query(nativeQuery = true, value = "select * from get_invoices(:types, :type_source, :lim, :offs, :from_date, :to_date, :active, :updated, :to_pay, :warehous_id, null)")
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

    @Query(
        nativeQuery = true,
        value = "select * from get_invoices(:types, null," + Int.MAX_VALUE + ", 0, :from_date, :to_date, :active, :updated, :to_pay, :warehouse_id, :client_id)",
        countProjection = "select count(*) get_invoices(:types, null, 0," + Int.MAX_VALUE + ",  :from_date, :to_date, :active, :updated, :to_pay, :warehouse_id, :client_id)"
    )
    fun getInvoicesPageable(
        @Param("types") types: String?,
        @Param("from_date") fromDate: LocalDate?,
        @Param("to_date") toDate: LocalDate?,
        @Param("active") active: Int,
        @Param("updated") updated: Int,
        @Param("to_pay") toPay: Int,
        @Param("client_id") dealerClientId: Long?,
        @Param("warehouse_id") warehouseId: Long?,
        pageable: Pageable?
    ): Page<InvoiceDtoProjection?>?

    @Query(
        nativeQuery = true, value = """SELECT     
                                           i.id as id,
                                           i.client_id as clientId,
                                           dc.printable_name as printableName,
                                           i.date as date,
                                           i.type as type,
                                           i.info as info,
                                           count(i.id) as itemCount,
                                           sum(iia.amount) as total
                                       FROM invoice i
                                       INNER JOIN dealer_client dc on dc.id = i.client_id
                                       INNER JOIN invoice_item ii on i.id = ii.invoice_id
                                       INNER JOIN invoice_item_amount iia on ii.id = iia.invoice_item
                                       GROUP BY i.id, dc.printable_name
                                       ORDER BY i.id DESC"""
    )
    fun getAllInvoices(): List<InvoiceProjection?>?
}

@Repository
interface ProductRepository : JpaRepository<RootEntity, Long> {
    @Query(nativeQuery = true, value = "select * from get_product_by_name(:param1, :param2, :param3, :param4)")
    fun getProductListByNameLike(
        @Param("param1") param1: String?,
        @Param("param2") param2: String?,
        @Param("param3") param3: String?,
        @Param("param4") param4: String?
    ): List<ProductDtoProjection?>?

    @Query(
        nativeQuery = true,
        value = "select * from get_product_by_name(:name, '', '', '')",
        countProjection = "select count(*) from get_product_by_name(:name, '', '', '')"
    )
    fun pageProductsByName(@Param("name") productName: String?, pageable: Pageable?): Page<ProductDtoProjection?>?

    @Query(
        nativeQuery = true,
        value = "select * from get_product_rates(:product_id, :type)",
        countProjection = "select count(*) from get_product_rates(:product_id, :type)"
    )
    fun pageProductPrices(
        @Param("product_id") productId: Long?,
        @Param("type") type: Int,
        pageable: Pageable?
    ): Page<ProductPriceDtoProjection?>?

    @Query(nativeQuery = true, value = "select last_value from product_id_seq")
    fun lastInsertProductId(): Long?


    @Query(
        nativeQuery = true,
        value = "select * from get_product_group(:name, null)",
        countProjection = "select count(*) from get_product_group(:name)"
    )
    fun pageProductGroupByName(
        @Param("name") groupName: String?,
        pageable: Pageable?
    ): Page<ProductGroupDtoProjection?>?

    @Query(nativeQuery = true, value = "select * from get_unit()", countProjection = "select count(*) from get_unit()")
    fun pageUnit(pageable: Pageable?): Page<UnitDtoProjection?>?

    @Query(nativeQuery = true, value = "select create_update_unit(:name, :symbol, :id)")
    fun createAndEditUnit(
        @Param("name") name: String?,
        @Param("symbol") symbol: String?,
        @Param("id") unitId: Long?
    ): Int?

    @Query(nativeQuery = true, value = "select create_update_product_group(:id, :name, :info, :parent_id)")
    fun createAndEditProductGroup(
        @Param("id") productGroupId: Long?,
        @Param("name") name: String?,
        @Param("info") info: String?,
        @Param("parent_id") groupId: Long?
    ): Int?

    @Query(nativeQuery = true, value = "select delete_product_group(:id)")
    fun deleteProductGroup(@Param("id") productGroupId: Long?): Boolean?

    @Query(nativeQuery = true, value = "select delete_unit(:id)")
    fun deleteUnit(@Param("id") unitId: Long?): Boolean?

    @Query(nativeQuery = true, value = "select id, name, symbol from unit where status = any(ARRAY[2, 5]) and id = :id")
    fun getUnitById(@Param("id") unitId: Long?): UnitDtoProjection?

    @Query(nativeQuery = true, value = "select * from get_product_group(null, :id)")
    fun getProductGroupById(@Param("id") productGroupId: Long?): ProductGroupDtoProjection?
}

@Repository
interface ReportRepository : JpaRepository<RootEntity, Long> {
    @Query(
        nativeQuery = true,
        value = "SELECT * FROM get_invoice_item_by_date(:from_date, :to_date, :items_per_page, :number_of_page)",
        countProjection = "SELECT COUNT(*) FROM get_invoice_item_by_date(:from_date, :to_date, :items_per_page, :number_of_page)"
    )
    fun getInvoiceItemByDate(
        @Param("from_date") fromDate: LocalDate?,
        @Param("to_date") toDate: LocalDate?,
        @Param("items_per_page") size: Int,
        @Param("number_of_page") page: Int,
        pageable: Pageable?
    ): Page<ReportByDateProjection?>?
}

@Repository
interface SettingRepository : JpaRepository<RootEntity, Long> {
    @Query(
        nativeQuery = true,
        value = "select * from get_companies()",
        countProjection = "select count(*) from get_companies()"
    )
    fun getCompanies(pageable: Pageable?): Page<CompanyDtoProjection?>?

    @Query(nativeQuery = true, value = "select * from currency where status = any(ARRAY[2, 5])")
    fun getAllCurrencies(): List<CurrencyDtoProjection?>?

    @Query(
        nativeQuery = true,
        value = "select create_update_company(:company_name, :is_main, :phone, :motto, :director, :manager, :telegram_contact, :email, :company_id)"
    )
    fun createOrUpdateCompany(
        @Param("company_name") companyName: String?, @Param("is_main") isMain: Boolean?, @Param("phone") phone: String?,
        @Param("motto") motto: String?, @Param("director") director: String?, @Param("manager") manager: String?,
        @Param("telegram_contact") telegramContact: String?, @Param("email") email: String?,
        @Param("company_id") company_id: Long?
    ): Long?

    @Query(nativeQuery = true, value = "select save_as_file(:file_name, :org_file_name)")
    fun createAsFile(@Param("file_name") fileName: String?, @Param("org_file_name") orgFileName: String?): Long?

    @Query(nativeQuery = true, value = "select set_company_img(:company_id, :logo_id, 'LOGO')")
    fun setLogo(@Param("company_id") companyId: Long?, @Param("logo_id") logoId: Long?): Int?

    @Query(nativeQuery = true, value = "select set_company_img(:company_id, :favicon_id, 'FAVICON')")
    fun setFavicon(@Param("company_id") companyId: Long?, @Param("favicon_id") faviconId: Long?): Int?

    @Query(nativeQuery = true, value = "select make_company_main(:company_id)")
    fun makeCompanyMain(@Param("company_id") companyId: Long?): Boolean?

    @Query(nativeQuery = true, value = "select delete_company(:company_id)")
    fun deleteCompany(@Param("company_id") companyId: Long?): Boolean?

    @Query(
        nativeQuery = true, value = """select af.file_name
                                       from company c
                                       left join as_file af on c.logo_id = af.id
                                       where c.is_main"""
    )
    fun getMainCompanyLogoName(): String?

    @Query(
        nativeQuery = true, value = """select af.file_name
                                       from company c
                                       left join as_file af on c.favicon_id = af.id
                                       where c.is_main"""
    )
    fun getMainCompanyFaviconName(): String?
}

@Repository
interface WareHouseRepository : JpaRepository<RootEntity, Long> {

    @Query(nativeQuery = true, value = "select * from get_warehouses_by_name(:name, 0, 10000000)")
    fun getAllWareHousesWithName(@Param("name") name: String?): List<WareHouseDtoProjection?>?

    @Query(
        nativeQuery = true,
        value = "select * from get_warehouses_by_name(:nameLike, :offset, :limit)",
        countProjection = "select count(*) from get_warehouses_by_name(:nameLike, :offset, :limit)"
    )
    fun getAllWareHousesWithName(
        @Param("nameLike") name: String?,
        @Param("offset") start: Int,
        @Param("limit") count: Int,
        pageable: Pageable?
    ): Page<WareHouseDtoProjection?>?

    @Query(nativeQuery = true, value = "select delete_warehouse(:warehouse_id)")
    fun deleteWareHouse(@Param("warehouse_id") warehouseId: Long?): Boolean?

    @Query(nativeQuery = true, value = "select save_warehouse(:name, :is_default)")
    fun saveWareHouse(@Param("name") name: String?, @Param("is_default") isDefault: Boolean?): Boolean?

    @Query(nativeQuery = true, value = "select edit_warehouse(:name, :is_default, :warehouse_id)")
    fun editWareHouse(
        @Param("name") name: String?,
        @Param("is_default") isDefault: Boolean,
        @Param("warehouse_id") warehouseId: Long?
    ): Boolean?
}

interface UserRepository: JpaRepository<RootEntity, Long>{
    @Query(nativeQuery = true, value = "select * from get_user(:login, :password)")
    fun getUser(@Param("login") login: String?, @Param("password") password: String?): Optional<UserDtoProjection?>?

    @Query(nativeQuery = true, value = "select * from get_user_by_login(:login)")
    fun findByLogin(@Param("login") login: String?): Optional<UserDtoProjection?>?
}