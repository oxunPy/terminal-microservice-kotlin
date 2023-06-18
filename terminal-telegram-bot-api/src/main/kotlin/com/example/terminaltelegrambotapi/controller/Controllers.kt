package com.example.terminaltelegrambotapi.controller

import com.example.common.constants.DateRangeAndCurrency
import com.example.common.dto.BotUserModel
import com.example.common.dto.ClientBalance
import com.example.terminaltelegrambotapi.service.RestTelegramService
import io.swagger.v3.oas.annotations.Operation
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal

@RestController
@RequestMapping("/bot/api")
class RestTelegramController(private val restTelegramService: RestTelegramService){
    @Operation(hidden = true)
    @PostMapping("/save-bot-user")
    fun saveBotUser(@RequestBody bum: BotUserModel?): BotUserModel? {
        return restTelegramService.saveBotUserModel(bum!!)
    }

    @PostMapping("/get-receipt-cash")
    @Operation(hidden = true)
    fun getReceiptCash(@RequestBody dateRangeAndCurrency: DateRangeAndCurrency): BigDecimal? {
        return restTelegramService.getReceiptCash(
            dateRangeAndCurrency.currency,
            dateRangeAndCurrency.fromDate,
            dateRangeAndCurrency.toDate
        )
    }

    @PostMapping("/get-receipt-bank")
    @Operation(hidden = true)
    fun getReceiptBank(@RequestBody dateRangeAndCurrency: DateRangeAndCurrency): BigDecimal? {
        return restTelegramService.getReceiptBank(
            dateRangeAndCurrency.currency,
            dateRangeAndCurrency.fromDate,
            dateRangeAndCurrency.toDate
        )
    }

    @PostMapping("/get-payment-cash")
    @Operation(hidden = true)
    fun getPaymentCash(@RequestBody dateRangeAndCurrency: DateRangeAndCurrency): BigDecimal? {
        return restTelegramService.getPaymentCash(
            dateRangeAndCurrency.currency,
            dateRangeAndCurrency.fromDate,
            dateRangeAndCurrency.toDate
        )
    }

    @PostMapping("/get-payment-bank")
    @Operation(hidden = true)
    fun getPaymentBank(@RequestBody dateRangeAndCurrency: DateRangeAndCurrency): BigDecimal? {
        return restTelegramService.getPaymentBank(
            dateRangeAndCurrency.currency,
            dateRangeAndCurrency.fromDate,
            dateRangeAndCurrency.toDate
        )
    }

    @PostMapping("/get-total-returned-amount-from-client")
    @Operation(hidden = true)
    fun getTotalReturnedAmount(@RequestBody dateRangeAndCurrency: DateRangeAndCurrency): BigDecimal? {
        return restTelegramService.getTotalReturnedAmountFromClient(
            dateRangeAndCurrency.currency,
            dateRangeAndCurrency.fromDate,
            dateRangeAndCurrency.toDate
        )
    }

    @Operation(hidden = true)
    @PostMapping("/get-total-balance-client")
    fun getTotalBalance(@RequestBody dateRangeAndCurrency: DateRangeAndCurrency): ClientBalance? {
        return restTelegramService.getTotalBalanceClient(
            dateRangeAndCurrency.currency,
            dateRangeAndCurrency.toDate
        )
    }

    @Operation(hidden = true)
    @GetMapping("/user-exists/{chatId}")
    fun userExists(@PathVariable("chatId") chatId: Long?): Boolean {
        return restTelegramService.userExists(chatId!!)!!
    }

    @Operation(hidden = true)
    @GetMapping("/get-user/{chatId}")
    fun getBotUser(@PathVariable("chatId") chatId: Long?): ResponseEntity<BotUserModel?>? {
        return ResponseEntity.ok().body(restTelegramService.getBotUser(chatId!!))
    }

    @Operation(hidden = true)
    @GetMapping("/activate/{chatId}")
    fun activateBotUser(@PathVariable("chatId") chatId: Long?): Boolean {
        return restTelegramService.activateUser(chatId!!)!!
    }

}