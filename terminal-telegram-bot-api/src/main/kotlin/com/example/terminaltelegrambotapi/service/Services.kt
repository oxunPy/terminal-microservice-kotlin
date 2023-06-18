package com.example.terminaltelegrambotapi.service

import com.example.common.constants.Currency
import com.example.common.dto.BotUserModel
import com.example.common.dto.ClientBalance
import java.math.BigDecimal
import java.time.LocalDate


interface RestTelegramService{
    fun getReceiptCash(currency: Currency?, fromDate: LocalDate?, toDate: LocalDate?): BigDecimal?

    fun getReceiptBank(currency: Currency?, fromDate: LocalDate?, toDate: LocalDate?): BigDecimal?

    fun getPaymentCash(currency: Currency?, fromDate: LocalDate?, toDate: LocalDate?): BigDecimal?

    fun getPaymentBank(currency: Currency?, fromDate: LocalDate?, toDate: LocalDate?): BigDecimal?

    fun getTotalReturnedAmountFromClient(currency: Currency?, fromDate: LocalDate?, toDate: LocalDate?): BigDecimal?

    fun getTotalBalanceClient(currency: Currency?, toDate: LocalDate?): ClientBalance?

    fun saveBotUserModel(bum: BotUserModel): BotUserModel?

    fun userExists(chatId: Long): Boolean?

    fun getBotUser(chatId: Long): BotUserModel?

    fun activateUser(chatId: Long): Boolean?
}