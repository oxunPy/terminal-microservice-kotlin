package com.example.common.dto

import com.example.common.constants.BotState
import com.example.common.constants.Currency
import com.example.common.constants.Status
import com.example.common.dto.data_interface.WareHouseDtoProjection
import org.springframework.beans.BeanUtils
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import javax.validation.constraints.NotNull

data class AccountDto(
    var id: Int? = null,

    var firstName: String? = null,

    var lastName: String? = null,

    var printableName: String? = null,

    var phoneNumber: String? = null
)

data class DealerDto(
    var id: Long? = null,

    var name: String? = null,

    var dealerCode: String? = null
)

data class ExchangeRateDto (
    var id: Long? = null,

    var invDate: LocalDateTime? = null,

    var mainCurrency: String? = null,

    var mainCurrencyVal: BigDecimal? = null,

    var toCurrency: String? = null,

    var toCurrencyVal: BigDecimal? = null,
)

data class InvoiceDto(
    var id: Long? = null,

    var accountId: Long? = null,

    var accountPrintableName: String? = null,

    var date: String? = null,

    var type: Int = 0,

    var info: String? = null,

    var status: Int? = null,

    var countOfItems: Long? = null,

    var total: BigDecimal? = null,

    var invoiceItemDtoList: List<InvoiceItemDto>? = null,
)

data class InvoiceItemDto(
    var id: Long? = null,

    var invoiceId: Long? = null,

    var productId: Long? = null,

    var productName: String? = null,

    var quantity: BigDecimal? = null,

    var rate: BigDecimal? = null,

    var sellingRate: BigDecimal? = null,

    var originalRate: BigDecimal? = null,

    var currencyCode: String? = null,

    var total: BigDecimal? = null,
)

data class PagedResponseDto<T> (
    var items: List<T>? = null,

    var totalElements: Long? = null,

    var totalSum: BigDecimal? = null
)

data class ProductDto(
    val id: Long? = null,

    val productName: String? = null,

    val rate: BigDecimal? = null,

    val originalRate: BigDecimal? = null,

    val currency: String? = null,

    val groupName: String? = null,
)

data class ReportByDateDto(
    var invoiceItemId: Int? = null,

    var dateOfSell: LocalDate? = null,

    var productQuantity: BigDecimal? = null,

    var productPrice: BigDecimal? = null,

    var typeOfOperation: Int? = null,

    var productName: String? = null,

    var priceCurrency: Int? = null,
)

data class ReportTodayIncomeExpenseDto(
    var income: Double? = null,
    var expense: Double? = null
)

data class UserDto(
    var userName: String? = null,

    var login: String? = null,

    var jwtToken: String? = null,

    var password: String? = null
)

data class WareHouseDto (
    var id: Long? = null,

    var name: String? = null,

    var isDefault: Boolean? = null
) {
    companion object {
        fun buildFromDtoProjection(@NotNull dtoProjection: WareHouseDtoProjection?): WareHouseDto {
            val dto = WareHouseDto()
            BeanUtils.copyProperties(dtoProjection!!, dto)
            return dto
        }
    }
}

data class ClientBalance(
    var debit: BigDecimal? = BigDecimal.ZERO,
    var credit: BigDecimal? = BigDecimal.ZERO
)

data class BotUserModel(
    var id: Long? = null,

    var chatId: Long? = null,

    var userName: String? = null,

    var firstName: String? = null,

    var lastName: String? = null,

    var contact: String? = null,

    var status: Status? = null,

    var command: String? = null,

    var botState: BotState = BotState.START_STATE,

    var currency: Currency? = null,

    var synced: Boolean? = false,

    var createdDate: Date? = null,

    var updatedDate: Date? = null
)
