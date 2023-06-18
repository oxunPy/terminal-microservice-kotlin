package com.example.terminaltelegrambot.handler.impl

import com.example.common.constants.Currency
import com.example.common.dto.BotUserModel
import com.example.terminaltelegrambot.calendar.CalendarMarkup
import com.example.terminaltelegrambot.calendar.CalendarMarkupUtils
import com.example.terminaltelegrambot.commands.Commands
import com.example.terminaltelegrambot.commands.operation.Operations
import com.example.terminaltelegrambot.handler.Handler
import com.example.terminaltelegrambot.service.TelegramService
import com.example.terminaltelegrambot.service.UserService
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup
import org.telegram.telegrambots.meta.api.objects.CallbackQuery
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton
import java.time.LocalDate
import java.time.Month

@Component
class CallBackQueryHandler(private val telegramService: TelegramService, private val operations: Operations, private val userService: UserService): Handler<CallbackQuery> {
    override fun handleMessage(callbackQuery: CallbackQuery) {
        val editMessageReplyMarkup = EditMessageReplyMarkup()
        editMessageReplyMarkup.chatId = callbackQuery.message.chatId.toString()
        editMessageReplyMarkup.messageId = callbackQuery.message.messageId
        editMessageReplyMarkup.inlineMessageId = callbackQuery.inlineMessageId
        val calendarUtils = CalendarMarkupUtils()
        val bum: BotUserModel = userService.getUserModel(callbackQuery.message.chatId)!!

        if (callbackQuery.data == CalendarMarkup.SUBMIT_DATE) {
            val fromDate: LocalDate = calendarUtils.getFromDateFromCalendar(callbackQuery.message.replyMarkup)!!
            val toDate: LocalDate = calendarUtils.getToDateFromCalendar(callbackQuery.message.replyMarkup)!!
            val command: String? = bum.command
            val currency: Currency? = bum.currency
            if (command == null) {
                telegramService.executeMessage(
                                                SendMessage.builder()
                                                    .text("Servis tanlanmadi!!!")
                                                    .chatId(callbackQuery.message.chatId.toString())
                                                    .replyMarkup(Commands.getWholeOperationKeyboard())
                                                    .build()
                )
                return
            }
            if (command == Commands.WHOLE_OPERATION) {
                telegramService.executeMessage(
                                                SendMessage.builder()
                                                    .text(operations.getWholePeriodOperations(currency!!, fromDate, toDate))
                                                    .chatId(callbackQuery.message.chatId.toString())
                                                    .replyMarkup(Commands.getDateMarkup())
                                                    .build()
                )
            } else {
                telegramService.executeMessage(
                                                SendMessage.builder()
                                                    .text(operations.getPeriodOperation(currency!!, fromDate, toDate, command))
                                                    .chatId(callbackQuery.message.chatId.toString())
                                                    .replyMarkup(Commands.getDateMarkup())
                                                    .build()
                                            )
            }
        } else if (callbackQuery.data == CalendarMarkupUtils.FROM_DATE + CalendarMarkup.PREV_YEAR) {
            val dateToDateCalendar: InlineKeyboardMarkup? = CalendarMarkupUtils().cloneDateToDateCalendar(callbackQuery.message.replyMarkup)
            editMessageReplyMarkup.replyMarkup = dateToDateCalendar
            val currentYearButton: InlineKeyboardButton? = calendarUtils.getButtonByCallbackQuery(dateToDateCalendar!!.keyboard,                CalendarMarkupUtils.FROM_DATE + CalendarMarkup.CURRENT_YEAR)
            currentYearButton!!.text = (currentYearButton.text.toInt() - 1).toString()
            telegramService.executeEditMessageReplyMarkup(editMessageReplyMarkup)
        }
        else if (callbackQuery.data == CalendarMarkupUtils.FROM_DATE + CalendarMarkup.NEXT_YEAR) {
            val dateToDateCalendar: InlineKeyboardMarkup? = calendarUtils.cloneDateToDateCalendar(callbackQuery.message.replyMarkup)
            editMessageReplyMarkup.replyMarkup = dateToDateCalendar
            val currentYearButton: InlineKeyboardButton? = CalendarMarkupUtils().getButtonByCallbackQuery(dateToDateCalendar!!.keyboard, CalendarMarkupUtils.FROM_DATE + CalendarMarkup.CURRENT_YEAR)
            currentYearButton!!.text = (currentYearButton.text.toInt() + 1).toString()
            telegramService.executeEditMessageReplyMarkup(editMessageReplyMarkup)
        }
        else if (callbackQuery.data == CalendarMarkupUtils.FROM_DATE + CalendarMarkup.PREV_MONTH) {
            val dateToDateCalendar: InlineKeyboardMarkup? = calendarUtils.cloneDateToDateCalendar(callbackQuery.message.replyMarkup)
            editMessageReplyMarkup.replyMarkup = dateToDateCalendar
            val currentMonthButton: InlineKeyboardButton? = calendarUtils.getButtonByCallbackQuery(dateToDateCalendar!!.keyboard,CalendarMarkupUtils.FROM_DATE + CalendarMarkup.CURRENT_MONTH)
            val monthVal = Month.valueOf(currentMonthButton!!.text).value
            val year: Int = calendarUtils.getButtonByCallbackQuery(dateToDateCalendar.keyboard,CalendarMarkupUtils.FROM_DATE + CalendarMarkup.CURRENT_YEAR)!!.text.toInt()
            calendarUtils.updateDaysFromDate(dateToDateCalendar, year, if (monthVal == 1) 12 else monthVal - 1)
            currentMonthButton.text = (if (monthVal == 1) Month.DECEMBER else Month.of(monthVal - 1)).toString()
            telegramService.executeEditMessageReplyMarkup(editMessageReplyMarkup)
        }
        else if (callbackQuery.data == CalendarMarkupUtils.FROM_DATE + CalendarMarkup.NEXT_MONTH) {
            val dateToDateCalendar: InlineKeyboardMarkup? = calendarUtils.cloneDateToDateCalendar(callbackQuery.message.replyMarkup)
            editMessageReplyMarkup.replyMarkup = dateToDateCalendar
            val currentMonthButton: InlineKeyboardButton? = calendarUtils.getButtonByCallbackQuery(dateToDateCalendar!!.keyboard,CalendarMarkupUtils.FROM_DATE + CalendarMarkup.CURRENT_MONTH)
            val monthVal = Month.valueOf(currentMonthButton!!.text).value
            val year: Int = calendarUtils.getButtonByCallbackQuery(dateToDateCalendar.keyboard, CalendarMarkupUtils.FROM_DATE + CalendarMarkup.CURRENT_YEAR)!!.text.toInt()
            calendarUtils.updateDaysFromDate(dateToDateCalendar, year, if (monthVal == 12) 1 else monthVal + 1)
            currentMonthButton.text = (if (monthVal == 12) Month.JANUARY else Month.of(monthVal + 1)).toString()
            telegramService.executeEditMessageReplyMarkup(editMessageReplyMarkup)
        }
        else if (callbackQuery.data == CalendarMarkupUtils.TO_DATE + CalendarMarkup.PREV_YEAR) {
            val dateToDateCalendar: InlineKeyboardMarkup? = calendarUtils.cloneDateToDateCalendar(callbackQuery.message.replyMarkup)
            editMessageReplyMarkup.replyMarkup = dateToDateCalendar
            val currentYearButton: InlineKeyboardButton? = calendarUtils.getButtonByCallbackQuery(dateToDateCalendar!!.keyboard, CalendarMarkupUtils.TO_DATE + CalendarMarkup.CURRENT_YEAR)
            currentYearButton!!.text = (currentYearButton.text.toInt() - 1).toString()
            telegramService.executeEditMessageReplyMarkup(editMessageReplyMarkup)
        }
        else if (callbackQuery.getData() == CalendarMarkupUtils.TO_DATE + CalendarMarkup.NEXT_YEAR) {
            val dateToDateCalendar: InlineKeyboardMarkup? = calendarUtils.cloneDateToDateCalendar(callbackQuery.message.replyMarkup)
            editMessageReplyMarkup.replyMarkup = dateToDateCalendar
            val currentYearButton: InlineKeyboardButton? = calendarUtils.getButtonByCallbackQuery(dateToDateCalendar!!.keyboard, CalendarMarkupUtils.TO_DATE + CalendarMarkup.CURRENT_YEAR)
            currentYearButton!!.text = (currentYearButton.text.toInt() + 1).toString()
            telegramService.executeEditMessageReplyMarkup(editMessageReplyMarkup)
        }
        else if (callbackQuery.data == CalendarMarkupUtils.TO_DATE + CalendarMarkup.PREV_MONTH) {
            val dateToDateCalendar: InlineKeyboardMarkup? = calendarUtils.cloneDateToDateCalendar(callbackQuery.message.replyMarkup)
            editMessageReplyMarkup.replyMarkup = dateToDateCalendar
            val currentMonthButton: InlineKeyboardButton? = calendarUtils.getButtonByCallbackQuery(dateToDateCalendar!!.keyboard, CalendarMarkupUtils.TO_DATE + CalendarMarkup.CURRENT_MONTH)
            val monthVal = Month.valueOf(currentMonthButton!!.text).value
            val yearButton: InlineKeyboardButton? = calendarUtils.getButtonByCallbackQuery(dateToDateCalendar.keyboard, CalendarMarkupUtils.TO_DATE + CalendarMarkup.CURRENT_YEAR)
            val year = yearButton?.text?.toInt() ?: LocalDate.now().year
            calendarUtils.updateDaysToDate(dateToDateCalendar, year, if (monthVal == 1) 12 else monthVal - 1)
            currentMonthButton.text = (if (monthVal == 1) Month.DECEMBER else Month.of(monthVal - 1)).toString()
            telegramService.executeEditMessageReplyMarkup(editMessageReplyMarkup)
        }
        else if (callbackQuery.getData() == CalendarMarkupUtils.TO_DATE + CalendarMarkup.NEXT_MONTH) {
            val dateToDateCalendar: InlineKeyboardMarkup? = calendarUtils.cloneDateToDateCalendar(callbackQuery.message.replyMarkup)
            editMessageReplyMarkup.replyMarkup = dateToDateCalendar
            val currentMonthButton: InlineKeyboardButton? = calendarUtils.getButtonByCallbackQuery(dateToDateCalendar!!.keyboard, CalendarMarkupUtils.TO_DATE + CalendarMarkup.CURRENT_MONTH)
            val monthVal = Month.valueOf(currentMonthButton!!.text).value
            val yearButton: InlineKeyboardButton? = calendarUtils.getButtonByCallbackQuery(dateToDateCalendar.keyboard, CalendarMarkupUtils.TO_DATE + CalendarMarkup.CURRENT_YEAR)
            val year = yearButton?.text?.toInt() ?: LocalDate.now().year
            calendarUtils.updateDaysToDate(dateToDateCalendar, year, if (monthVal == 12) 1 else monthVal + 1)
            currentMonthButton.text = (if (monthVal == 12) Month.JANUARY else Month.of(monthVal + 1)).toString()
            telegramService.executeEditMessageReplyMarkup(editMessageReplyMarkup)
        }
        else if (callbackQuery.data.startsWith(CalendarMarkupUtils.FROM_DATE) && calendarUtils.hasMonthDayNumber(callbackQuery.data)) {
            val dateToDateCalendar: InlineKeyboardMarkup? = calendarUtils.cloneDateToDateCalendar(callbackQuery.message.replyMarkup)
            editMessageReplyMarkup.replyMarkup = dateToDateCalendar
            val oldActiveButton: InlineKeyboardButton? = calendarUtils.getActiveDay(dateToDateCalendar!!, CalendarMarkup.ACTIVE_FROM_DATE_SIGN)
            if (oldActiveButton != null) oldActiveButton.text = oldActiveButton.text.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
            val activeDay: InlineKeyboardButton? = calendarUtils.getButtonByCallbackQuery(dateToDateCalendar.keyboard, callbackQuery.data)
            activeDay!!.text = activeDay.text + " " + CalendarMarkup.ACTIVE_FROM_DATE_SIGN
            telegramService.executeEditMessageReplyMarkup(editMessageReplyMarkup)
        }
        else if (callbackQuery.data.startsWith(CalendarMarkupUtils.TO_DATE) && calendarUtils.hasMonthDayNumber(callbackQuery.data)) {
            val dateToDateCalendar: InlineKeyboardMarkup? = calendarUtils.cloneDateToDateCalendar(callbackQuery.message.replyMarkup)
            editMessageReplyMarkup.replyMarkup = dateToDateCalendar
            val oldActiveButton: InlineKeyboardButton? = calendarUtils.getActiveDay(dateToDateCalendar!!, CalendarMarkup.ACTIVE_TO_DATE_SIGN)
            if (oldActiveButton != null) oldActiveButton.text = oldActiveButton.text.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
            val activeDay: InlineKeyboardButton? = calendarUtils.getButtonByCallbackQuery(dateToDateCalendar.keyboard, callbackQuery.data)
            activeDay!!.text = activeDay.text + " " + CalendarMarkup.ACTIVE_TO_DATE_SIGN
            telegramService.executeEditMessageReplyMarkup(editMessageReplyMarkup)
        }
    }
}