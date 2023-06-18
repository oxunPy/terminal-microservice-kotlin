package com.example.terminaltelegrambot.commands

import com.example.common.constants.BotState
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow

class Commands {
    companion object{
        const val START = "/start"
        const val MENU = "Меню"
        const val REMAINING_GOODS = "Остаток товари"
        const val AUDIO = "audio"
        const val VIDEO = "video"
        const val MESSAGE = "message"
        const val SYNCHRONIZE = "Синхронизировать"
        const val REQUESTADMIN = "request"
        const val GET_ALL_SYNC = "Я хочу получить все"

        // VALUTA
        const val UZBEK_CURRENCY = "UZS"
        const val AMERICAN_CURRENCY = "USD"

        //OPERATIONS
        const val GET_PAYMENT_CASH = "Списание наличие дс"
        const val GET_PAYMENT_BANK = "Списание без наличных дс"
        const val GET_RECEIPT_CASH = "Поступление наличие дс"
        const val GET_RECEIPT_BANK = "Поступление без наличных дс"
        const val GET_TOTAL_RETURNED_AMOUNT_FROM_CLIENT = "Сумма возвратных товоров"
        const val GET_TOTAL_BALANCE_CLIENT = "Общий баланс клиента"

        const val WHOLE_OPERATION = "Все операция"

        // DATE
        const val TODAY_OPERATION = "Сегодняшная операция"
        const val THIS_WEEKEND = "Этот неделя"
        const val THIS_MONTH = "Этот месяц"
        const val OTHER_DATE = "Другие дата"
        const val BACK = "Назад"

        const val UPDATE = "Обновить"

        fun getAdditionalFunctionsMenuKeyBoard(): ReplyKeyboardMarkup {
            val markup = ReplyKeyboardMarkup()
            markup.resizeKeyboard = true
            markup.oneTimeKeyboard = true
            markup.selective = true
            val rows: MutableList<KeyboardRow> = ArrayList()
            val row1 = KeyboardRow()
            val row2 = KeyboardRow()
            val row3 = KeyboardRow()
            val paymentCashButton = KeyboardButton()
            paymentCashButton.text = GET_PAYMENT_CASH
            row1.add(paymentCashButton)
            val paymentBankButton = KeyboardButton()
            paymentBankButton.text = GET_PAYMENT_BANK
            row1.add(paymentBankButton)
            val receiptCashButton = KeyboardButton()
            receiptCashButton.text = GET_RECEIPT_CASH
            row1.add(receiptCashButton)
            val receiptBankButton = KeyboardButton()
            receiptBankButton.text = GET_RECEIPT_BANK
            row2.add(receiptBankButton)
            val returnedAmountButton = KeyboardButton()
            returnedAmountButton.text =
                GET_TOTAL_RETURNED_AMOUNT_FROM_CLIENT
            row2.add(returnedAmountButton)
            val totalBalanceButton = KeyboardButton()
            totalBalanceButton.text = GET_TOTAL_BALANCE_CLIENT
            row2.add(totalBalanceButton)
            rows.add(row1)
            rows.add(row2)
            rows.add(row3)
            markup.keyboard = rows
            return markup
        }

        fun getShareContactKeyBoard(): ReplyKeyboardMarkup? {
            val markup = ReplyKeyboardMarkup()
            markup.resizeKeyboard = true
            markup.oneTimeKeyboard = false
            markup.selective = true
            val rows: MutableList<KeyboardRow> = ArrayList()
            val row = KeyboardRow()
            val buttonAskContact = KeyboardButton()
            buttonAskContact.requestContact = true
            buttonAskContact.text = "Пожалуйста, поделитесь контактом"
            row.add(buttonAskContact)
            rows.add(row)
            markup.keyboard = rows
            return markup
        }

        fun getWholeOperationKeyboard(): ReplyKeyboardMarkup? {
            val markup = ReplyKeyboardMarkup()
            markup.oneTimeKeyboard = true
            markup.selective = true
            markup.resizeKeyboard = true
            val rows: MutableList<KeyboardRow> = ArrayList()
            val firstRow = KeyboardRow()
            val wholeOperationButton = KeyboardButton()
            wholeOperationButton.text = WHOLE_OPERATION
            val additionalFunctionsMarkup = getAdditionalFunctionsMenuKeyBoard()
            firstRow.add(wholeOperationButton)
            rows.add(firstRow)
            rows.addAll(additionalFunctionsMarkup.keyboard)
            markup.keyboard = rows
            return markup
        }

        fun getCurrencyKeyboardMarkup(): ReplyKeyboardMarkup? {
            val markup = ReplyKeyboardMarkup()
            markup.oneTimeKeyboard = true
            markup.selective = true
            markup.resizeKeyboard = true
            val rows: MutableList<KeyboardRow> = ArrayList()
            val row1 = KeyboardRow()
            val row2 = KeyboardRow()
            val uzsButton = KeyboardButton()
            uzsButton.text = UZBEK_CURRENCY
            val usdButton = KeyboardButton()
            usdButton.text = AMERICAN_CURRENCY
            val backButton = KeyboardButton()
            backButton.text = BACK
            row1.add(uzsButton)
            row1.add(usdButton)
            row2.add(backButton)
            rows.add(row1)
            rows.add(row2)
            markup.keyboard = rows
            return markup
        }

        fun getDateMarkup(): ReplyKeyboardMarkup? {
            val markup = ReplyKeyboardMarkup()
            markup.oneTimeKeyboard = true
            markup.selective = true
            markup.resizeKeyboard = true
            val rows: MutableList<KeyboardRow> = ArrayList()
            val row1 = KeyboardRow()
            val row2 = KeyboardRow()
            val row3 = KeyboardRow()
            val todayOperationButton = KeyboardButton()
            todayOperationButton.text = TODAY_OPERATION
            val thisWeekendButton = KeyboardButton()
            thisWeekendButton.text = THIS_WEEKEND
            val thisMonthButton = KeyboardButton()
            thisMonthButton.text = THIS_MONTH
            val otherDate = KeyboardButton()
            otherDate.text = OTHER_DATE
            val backButton = KeyboardButton()
            backButton.text = BACK
            row1.add(todayOperationButton)
            row1.add(thisWeekendButton)
            row2.add(thisMonthButton)
            row2.add(otherDate)
            row3.add(backButton)
            rows.add(row1)
            rows.add(row2)
            rows.add(row3)
            markup.keyboard = rows
            return markup
        }

        fun getCalendarMarkup(): ReplyKeyboardMarkup? {
            val markup = ReplyKeyboardMarkup()
            markup.selective = true
            markup.resizeKeyboard = true
            markup.oneTimeKeyboard = true
            val rows: MutableList<KeyboardRow> = ArrayList()
            val row = KeyboardRow()
            val backBtn = KeyboardButton(BACK)
            row.add(backBtn)
            rows.add(row)
            markup.keyboard = rows
            return markup
        }

        fun getUpdateMarkup(): ReplyKeyboardMarkup? {
            val markup = ReplyKeyboardMarkup()
            markup.selective = true
            markup.resizeKeyboard = true
            markup.oneTimeKeyboard = true
            val rows: MutableList<KeyboardRow> = ArrayList()
            val row = KeyboardRow()
            val updateBtn = KeyboardButton(UPDATE)
            row.add(updateBtn)
            rows.add(row)
            markup.keyboard = rows
            return markup
        }

        fun getMenuMarkupBy(botState: BotState?): ReplyKeyboardMarkup? {
            when (botState) {
                BotState.START_STATE -> return getWholeOperationKeyboard()
                BotState.CURRENCY_STATE -> return getCurrencyKeyboardMarkup()
                BotState.DATE_STATE -> return getDateMarkup()
                BotState.CALENDAR_STATE -> return getCalendarMarkup()
                else -> return getWholeOperationKeyboard()
            }
        }
    }
}