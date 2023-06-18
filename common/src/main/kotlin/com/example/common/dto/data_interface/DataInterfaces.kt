package com.example.common.dto.data_interface

import com.example.common.constants.BotState
import com.example.common.constants.Currency
import com.example.common.constants.Status
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

interface DataInterfaces {
    fun getDebit(): BigDecimal?

    fun getCredit(): BigDecimal?
}

interface UserInterface {
    fun getId(): Long?

    fun getStatus(): Status?

    fun getBotState(): BotState?

    fun getChatId(): Long?

    fun getFirstName(): String?

    fun getLastName(): String?

    fun getUserName(): String?

    fun getCommand(): String?

    fun getContact(): String?

    fun getCurrency(): Currency?
}

interface AccountDtoProjection {
    //TODO need to search annotation for fields
    fun getId(): Int?

    fun getFirst_name(): String?

    fun getLast_name(): String?

    fun getPrintable_name(): String?

    fun getPhone(): String?

    fun getOpening_balance(): BigDecimal?
}

interface CompanyDtoProjection {
    fun getId(): Long?

    fun getCompanyName(): String?

    fun getFaviconImgName(): String?

    fun getLogoImgName(): String?

    fun getManager(): String?

    fun getDirector(): String?

    fun getEmail(): String?

    fun getIsMain(): Boolean?

    fun getMotto(): String?

    fun getPhoneNumber(): String?

    fun getTelegramContact(): String?
}

interface CurrencyDtoProjection {
    fun getId(): Long?

    fun getCode(): String?
}

interface DealerDtoProjection {
    fun getId(): Long?

    fun getName(): String?

    fun getDealerCode(): String?
}

interface ExchangeRateDtoProjection {
    fun getId(): Long?

    fun getInv_date(): LocalDateTime?

    fun getMain_currency(): String?

    fun getMain_currency_val(): BigDecimal?

    fun getTo_currency(): String?

    fun getTo_currency_val(): BigDecimal?
}

interface InvoiceDtoProjection {
    fun getId(): Long?

    fun getAccount_id(): Long?

    fun getWarehouseId(): Long?

    fun getWarehouse(): String?

    fun getAccount_printable_name(): String?

    fun getDate(): String?

    fun getType(): Int?

    fun getStatus(): Int?

    fun getCount_of_items(): Long?

    fun getTotal(): BigDecimal?

    fun getInfo(): String?
}

interface InvoiceItemDtoProjection {
    fun getId(): Long?

    fun getProduct_id(): Long?

    fun getProduct_name(): String?

    fun getCurrencyCode(): String?

    fun getQuantity(): BigDecimal?

    fun getSelling_rate(): BigDecimal?

    fun getTotal(): BigDecimal?
}

interface InvoiceProjection {
    fun getId(): Long?

    fun getClientId(): Long?

    fun getWarehouseId(): Long?

    fun getWarehouse(): String?

    fun getPrintableName(): String?

    fun getDate(): Date?

    fun getType(): Int?

    fun getInfo(): String?

    fun getItemCount(): Long?

    fun getTotal(): BigDecimal?
}

interface PagedResponseDtoProjection {
    fun getCount(): Long?

    fun getSum(): BigDecimal?
}


interface ProductDtoProjection {
    fun getId(): Long?

    fun getProduct_name(): String?

    fun getRate(): BigDecimal?

    fun getCurrency(): String?

    fun getGroup_name(): String?

    fun getBarcode(): String?
}

interface ProductGroupDtoProjection {
    fun getId(): Long?

    fun getGroupName(): String?

    fun getInfo(): String?

    fun getParentId(): Long?

    fun getParentName(): String?
}

interface ProductPriceDtoProjection {
    fun getId(): Long?

    fun getDate(): String?

    fun getRate(): BigDecimal?

    fun getCurrency(): String?
}

interface ReportByDateProjection {
    fun getInvoice_item_id(): Int?

    fun getDate_of_sell(): LocalDate?

    fun getProduct_quantity(): BigDecimal?

    fun getProduct_price(): BigDecimal?

    fun getType_of_operation(): Int?

    fun getProduct_name(): String?

    fun getPrice_currency(): Int?
}

interface ReportTodayIncomeExpenseProjection {
    fun getIncome(): Double?

    fun getExpense(): Double?
}

interface UnitDtoProjection {
    fun getId(): Long?

    fun getName(): String?

    fun getSymbol(): String?
}

interface UserDtoProjection {
    fun getId(): Long?

    fun getInfo(): String?

    fun getUser_name(): String?

    fun getLogin(): String?

    fun getPass(): String?

    fun getRole(): String?
}

interface WareHouseDtoProjection {
    fun getId(): Long?

    fun getName(): String?

    fun getIsDefault(): Boolean?
}

interface BalanceInterface{
    fun getDebit(): BigDecimal?

    fun getCredit(): BigDecimal?
}
