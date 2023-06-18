package com.example.terminaltelegrambot.commands.operation

import com.example.terminaltelegrambot.commands.Commands
import com.example.terminaltelegrambot.service.UserService
import org.springframework.stereotype.Service
import java.time.DayOfWeek
import java.time.LocalDate
import java.util.*
import com.example.common.constants.Currency

@Service("Operation")
class Operations(private val userService: UserService) {
    fun getTodayOperations(currency: Currency, command: String): String? {
        return if (command == Commands.WHOLE_OPERATION) "Cегодня:   \n\n" + getWholePeriodOperations(
            currency,
            LocalDate.now().minusDays(1),
            LocalDate.now()
        ) else "Cегодня:   \n\n" + getPeriodOperation(currency, LocalDate.now().minusDays(1), LocalDate.now(), command)
    }

    fun getThisWeekendOperations(currency: Currency, command: String): String? {
        val now = LocalDate.now()
        val calendar = Calendar.getInstance()
        val dayOfWeek = calendar[Calendar.DAY_OF_WEEK]
        return if (command == Commands.WHOLE_OPERATION) "На этой неделе:   \n\n" + getWholePeriodOperations(
            currency,
            now.minusDays((dayOfWeek - DayOfWeek.MONDAY.ordinal).toLong()),
            now
        ) else "На этой неделе:   \n\n" + getPeriodOperation(
            currency,
            now.minusDays((dayOfWeek - DayOfWeek.MONDAY.ordinal).toLong()),
            now,
            command
        )
    }

    fun getThisMonthOperations(currency: Currency, command: String): String? {
        val now = LocalDate.now()
        return if (command == Commands.WHOLE_OPERATION) "В этом месяце:   \n\n" + getWholePeriodOperations(
            currency,
            LocalDate.of(now.year, now.month, 1),
            now
        ) else "В этом месяце:   \n\n" + getPeriodOperation(
            currency,
            LocalDate.of(now.year, now.month, 1),
            now,
            command
        )
    }

    fun getPeriodOperation(currency: Currency, fromDate: LocalDate?, toDate: LocalDate?, command: String?): String {
        when (command) {
            Commands.GET_PAYMENT_BANK -> return capitalize(Commands.GET_PAYMENT_BANK) + ":   " + userService.getPaymentBank(
                currency,
                fromDate,
                toDate
            ) + " " + currency

            Commands.GET_PAYMENT_CASH -> return capitalize(Commands.GET_PAYMENT_CASH) + ":   " + userService.getPaymentCash(
                currency,
                fromDate,
                toDate
            ) + " " + currency

            Commands.GET_RECEIPT_BANK -> return capitalize(Commands.GET_RECEIPT_BANK) + ":   " + userService.getReceiptBank(
                currency,
                fromDate,
                toDate
            ) + " " + currency

            Commands.GET_RECEIPT_CASH -> return capitalize(Commands.GET_RECEIPT_CASH) + ":   " + userService.getReceiptCash(
                currency,
                fromDate,
                toDate
            ) + " " + currency

            Commands.GET_TOTAL_BALANCE_CLIENT -> return """${capitalize(Commands.GET_TOTAL_BALANCE_CLIENT)}:
    кредит = ${userService.getTotalBalance(currency, toDate)!!.credit} $currency
    списание средств = ${userService.getTotalBalance(currency, toDate)!!.debit}""" + " " + currency

            Commands.GET_TOTAL_RETURNED_AMOUNT_FROM_CLIENT -> return (Commands.GET_TOTAL_RETURNED_AMOUNT_FROM_CLIENT + ":   " + userService.getTotalReturnedAmount(
                currency,
                fromDate,
                toDate
            )).toString() + " " + currency
        }
        return ""
    }

    fun getWholePeriodOperations(
        currency: Currency,
        fromDate: LocalDate?,
        toDate: LocalDate?
    ): String {
        return ((((((((((((
                Commands.GET_PAYMENT_CASH + ":   " + userService.getPaymentCash(currency, fromDate, toDate)) + " " + currency + "\n\n" +
                Commands.GET_PAYMENT_BANK) + ":   " + userService.getPaymentBank(currency, fromDate, toDate)) + " " + currency + "\n\n" +
                Commands.GET_RECEIPT_CASH) + ":   " + userService.getReceiptCash(currency, fromDate, toDate)) + " " + currency + "\n\n" +
                Commands.GET_RECEIPT_BANK) + ":   " + userService.getReceiptBank(currency, fromDate, toDate)) + " " + currency + "\n\n" +
                Commands.GET_TOTAL_RETURNED_AMOUNT_FROM_CLIENT) + ":   " + userService.getTotalReturnedAmount(currency, fromDate, toDate)) + " " + currency + "\n\n" +
                Commands.GET_TOTAL_BALANCE_CLIENT) + ":\n" + "    кредит =  " + userService.getTotalBalance(currency, toDate)!!.credit) + " " + currency + "\n" + "    списание средств =  " + userService.getTotalBalance(currency, toDate)!!.debit) + " " + currency
    }

    private fun capitalize(str: String?): String? {
        return if (str.isNullOrEmpty()) {
            str
        } else str.substring(0, 1).uppercase(Locale.getDefault()) + str.substring(1)
    }
}
