package com.example.terminaltelegrambot.service

import com.example.common.constants.Currency
import com.example.common.constants.DateRangeAndCurrency
import com.example.common.dto.BotUserModel
import com.example.common.dto.ClientBalance
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import java.math.BigDecimal
import java.time.LocalDate

@Component
class UserService(private val restTemplate: RestTemplate) {
    private val baseUrl = "http://localhost:8084/bot/api"

    fun saveUser(botUserModel: BotUserModel?): BotUserModel? {
        return restTemplate.postForObject("$baseUrl/save-bot-user", botUserModel, BotUserModel::class.java)
    }

    fun getReceiptCash(currency: Currency?, fromDate: LocalDate?, toDate: LocalDate?): BigDecimal? {
        val dateRangeAndCurrency = DateRangeAndCurrency(currency, fromDate, toDate)
        return restTemplate.postForObject("$baseUrl/get-receipt-cash", dateRangeAndCurrency, BigDecimal::class.java)
    }

    fun getReceiptBank(currency: Currency?, fromDate: LocalDate?, toDate: LocalDate?): BigDecimal? {
        val dateRangeAndCurrency = DateRangeAndCurrency(currency, fromDate, toDate)
        return restTemplate.postForObject("$baseUrl/get-receipt-bank", dateRangeAndCurrency, BigDecimal::class.java)
    }

    fun getPaymentCash(currency: Currency?, fromDate: LocalDate?, toDate: LocalDate?): BigDecimal? {
        val dateRangeAndCurrency = DateRangeAndCurrency(currency, fromDate, toDate)
        return restTemplate.postForObject("$baseUrl/get-payment-cash", dateRangeAndCurrency, BigDecimal::class.java)
    }

    fun getPaymentBank(currency: Currency?, fromDate: LocalDate?, toDate: LocalDate?): BigDecimal? {
        val dateRangeAndCurrency = DateRangeAndCurrency(currency, fromDate, toDate)
        return restTemplate.postForObject("$baseUrl/get-payment-bank", dateRangeAndCurrency, BigDecimal::class.java)
    }

    fun getTotalReturnedAmount(currency: Currency?, fromDate: LocalDate?, toDate: LocalDate?): BigDecimal? {
        val dateRangeAndCurrency = DateRangeAndCurrency(currency, fromDate, toDate)
        return restTemplate.postForObject("$baseUrl/get-total-returned-amount-from-client", dateRangeAndCurrency, BigDecimal::class.java)
    }

    fun getTotalBalance(currency: Currency?, toDate: LocalDate?): ClientBalance? {
        val dateRangeAndCurrency = DateRangeAndCurrency(currency, null, toDate)
        return restTemplate.postForObject("$baseUrl/get-total-balance-client", dateRangeAndCurrency, ClientBalance::class.java)
    }

    fun userExists(chatId: Long): Boolean? {
        return RestTemplate().getForObject<Boolean>("$baseUrl/user-exists/$chatId", Boolean::class.java)
    }

    fun getUserModel(chatId: Long): BotUserModel? {
        return RestTemplate().getForObject("$baseUrl/get-user/$chatId", BotUserModel::class.java)
    }

    fun requestToActivateUser(chatId: Long): Boolean {
        return java.lang.Boolean.TRUE == RestTemplate().getForObject<Boolean>("$baseUrl/activate/$chatId", Boolean::class.java)
    }
}