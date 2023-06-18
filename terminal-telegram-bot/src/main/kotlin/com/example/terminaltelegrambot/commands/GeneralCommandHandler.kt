package com.example.terminaltelegrambot.commands

import com.example.common.constants.BotState
import com.example.common.constants.Currency
import com.example.common.constants.Status
import com.example.common.dto.BotUserModel
import com.example.terminaltelegrambot.calendar.CalendarMarkup
import com.example.terminaltelegrambot.calendar.CalendarMarkupUtils
import com.example.terminaltelegrambot.commands.operation.Operations
import com.example.terminaltelegrambot.service.TelegramService
import com.example.terminaltelegrambot.service.UserService
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup

@Service
class GeneralCommandHandler(private val telegramService: TelegramService, private val userService: UserService, private val operations: Operations): CommandHandler<Message> {
    override fun executeCommand(message: Message?, command: String?) {
        val userModel: BotUserModel? = userService.getUserModel(message!!.chatId)
        val currency: Currency? = if (userModel != null) userModel.currency else Currency.USD
        val commandMethod: String? = userModel?.command

        if (command == Commands.START) {
            if (!userService.userExists(message.chatId)!!) { // creating user
                telegramService.executeMessage(
                                                SendMessage.builder()
                                                    .text("Пожалуйста, поделитесь контактом")
                                                    .chatId(message.chatId.toString())
                                                    .replyMarkup(Commands.getShareContactKeyBoard())
                                                    .build()
                )
            } else if (userModel != null && arrayOf(Status.ACTIVE, Status.CREATED).contains(userModel.status)) {
                telegramService.executeMessage(
                                                SendMessage.builder()
                                                    .text(Commands.WHOLE_OPERATION)
                                                    .chatId(message.chatId.toString())
                                                    .replyMarkup(Commands.getWholeOperationKeyboard())
                                                    .build()
                )
            }
        } else if (command == Commands.UPDATE) {
            // make bot user status active
            if (userService.requestToActivateUser(message.chatId)) {
                telegramService.executeMessage(
                                                SendMessage.builder()
                                                    .text("Пользователь создан с активным статусом!!!")
                                                    .chatId(message.chatId.toString())
                                                    .build()
                )
                telegramService.executeMessage(
                                                SendMessage.builder()
                                                    .text(Commands.WHOLE_OPERATION)
                                                    .chatId(message.chatId.toString())
                                                    .replyMarkup(Commands.getWholeOperationKeyboard())
                                                    .build()
                )
            } else {
                telegramService.executeMessage(
                                                SendMessage.builder()
                                                    .text("Отправьте запрос администратору на разрешение")
                                                    .chatId(message.chatId.toString())
                                                    .replyMarkup(Commands.getUpdateMarkup())
                                                    .build()
                )
            }
        } else if (userModel != null && arrayOf(Status.ACTIVE, Status.CREATED).contains(userModel.status)) {
            when (command) {
                Commands.WHOLE_OPERATION -> {
                    userModel.command = Commands.WHOLE_OPERATION
                    userModel.botState = BotState.CURRENCY_STATE
                    userService.saveUser(userModel)
                    telegramService.executeMessage(
                                                    SendMessage.builder()
                                                        .text("whole operations")
                                                        .chatId(message.chatId.toString())
                                                        .replyMarkup(Commands.getCurrencyKeyboardMarkup())
                                                        .build()
                    )
                }

                Commands.UZBEK_CURRENCY -> {
                    userModel.botState = BotState.DATE_STATE
                    userModel.currency = Currency.UZS
                    userService.saveUser(userModel)
                    telegramService.executeMessage(
                                                    SendMessage.builder()
                                                        .text("uzs")
                                                        .chatId(message.chatId.toString())
                                                        .replyMarkup(Commands.getDateMarkup())
                                                        .build()
                    )
                }

                Commands.AMERICAN_CURRENCY -> {
                    userModel.botState =BotState.DATE_STATE
                    userModel.currency = Currency.USD
                    userService.saveUser(userModel)
                    telegramService.executeMessage(
                                                    SendMessage.builder()
                                                        .text("usd")
                                                        .chatId(message.chatId.toString())
                                                        .replyMarkup(Commands.getDateMarkup())
                                                        .build()
                    )
                }

                Commands.TODAY_OPERATION -> telegramService.executeMessage(
                                                                            SendMessage.builder()
                                                                                .text(operations.getTodayOperations(currency!!, commandMethod!!)!!)
                                                                                .chatId(message.chatId.toString())
                                                                                .replyMarkup(Commands.getDateMarkup())
                                                                                .build()
                )

                Commands.THIS_WEEKEND -> telegramService.executeMessage(
                                                                            SendMessage.builder()
                                                                                .text(operations.getThisWeekendOperations(currency!!, commandMethod!!)!!)
                                                                                .chatId(message.chatId.toString())
                                                                                .replyMarkup(Commands.getDateMarkup())
                                                                                .build()
                )

                Commands.THIS_MONTH -> telegramService.executeMessage(
                                                                            SendMessage.builder()
                                                                                .text(operations.getThisMonthOperations(currency!!, commandMethod!!)!!)
                                                                                .chatId(message.chatId.toString())
                                                                                .replyMarkup(Commands.getDateMarkup())
                                                                                .build()
                )

                Commands.OTHER_DATE -> {
                    val dateToDateCalendar: InlineKeyboardMarkup = CalendarMarkupUtils().getFromDateToDateCalendar()!!
                    val toDateCalendar: InlineKeyboardMarkup =
                        CalendarMarkupUtils().convertToToDateCalendarMarkup(CalendarMarkup().getCalendarInstance())!!
                    telegramService.executeMessage(
                                                    SendMessage.builder()
                                                        .text("Выберите другую дату")
                                                        .chatId(message.chatId.toString())
                                                        .replyMarkup(Commands.getCalendarMarkup())
                                                        .build()
                    )
                    telegramService.executeMessage(
                                                    SendMessage.builder()
                                                        .text("Другая дата")
                                                        .chatId(message.chatId.toString())
                                                        .replyMarkup(if (commandMethod != null && commandMethod == Commands.GET_TOTAL_BALANCE_CLIENT) toDateCalendar else dateToDateCalendar)
                                                        .build()
                    )
                }

                Commands.GET_PAYMENT_CASH -> {
                    userModel.command = Commands.GET_PAYMENT_CASH
                    userModel.botState = BotState.CURRENCY_STATE
                    userService.saveUser(userModel)
                    telegramService.executeMessage(
                                                    SendMessage.builder()
                                                        .text("get payment cash")
                                                        .chatId(message.chatId.toString())
                                                        .replyMarkup(Commands.getCurrencyKeyboardMarkup())
                                                        .build()
                    )
                }

                Commands.GET_PAYMENT_BANK -> {
                    userModel.command = Commands.GET_PAYMENT_CASH
                    userModel.botState = BotState.CURRENCY_STATE
                    userService.saveUser(userModel)
                    telegramService.executeMessage(
                                                    SendMessage.builder()
                                                        .text("get payment bank")
                                                        .chatId(message.chatId.toString())
                                                        .replyMarkup(Commands.getCurrencyKeyboardMarkup())
                                                        .build()
                    )
                }

                Commands.GET_RECEIPT_BANK -> {
                    userModel.command = Commands.GET_PAYMENT_CASH
                    userModel.botState = BotState.CURRENCY_STATE
                    userService.saveUser(userModel)
                    telegramService.executeMessage(
                                                    SendMessage.builder()
                                                        .text("get receipt bank")
                                                        .chatId(message.chatId.toString())
                                                        .replyMarkup(Commands.getCurrencyKeyboardMarkup())
                                                        .build()
                    )
                }

                Commands.GET_RECEIPT_CASH -> {
                    userModel.command = Commands.GET_PAYMENT_CASH
                    userModel.botState = BotState.CURRENCY_STATE
                    userService.saveUser(userModel)
                    telegramService.executeMessage(
                        SendMessage.builder()
                            .text("get reciept cash")
                            .chatId(message.chatId.toString())
                            .replyMarkup(Commands.getCurrencyKeyboardMarkup())
                            .build()
                    )
                }

                Commands.GET_TOTAL_BALANCE_CLIENT -> {
                    userModel.command = Commands.GET_PAYMENT_CASH
                    userModel.botState = BotState.CURRENCY_STATE
                    userService.saveUser(userModel)
                    telegramService.executeMessage(
                                                    SendMessage.builder()
                                                        .text("get total balance client")
                                                        .chatId(message.chatId.toString())
                                                        .replyMarkup(Commands.getCurrencyKeyboardMarkup())
                                                        .build()
                    )
                }

                Commands.GET_TOTAL_RETURNED_AMOUNT_FROM_CLIENT -> {
                    userModel.command = Commands.GET_PAYMENT_CASH
                    userModel.botState = BotState.CURRENCY_STATE
                    userService.saveUser(userModel)
                    telegramService.executeMessage(
                                                    SendMessage.builder()
                                                        .text("get total returned amount from client")
                                                        .chatId(message.chatId.toString())
                                                        .replyMarkup(Commands.getCurrencyKeyboardMarkup())
                                                        .build()
                    )
                }

                Commands.BACK -> {
                    userModel.botState = if (userModel.botState.getParentBotState() != null) userModel.botState.getParentBotState()!! else BotState.START_STATE
                    userService.saveUser(userModel)
                    telegramService.executeMessage(
                                                    SendMessage.builder()
                                                        .text(Commands.BACK)
                                                        .chatId(message.chatId.toString())
                                                        .replyMarkup(Commands.getMenuMarkupBy(userModel.botState))
                                                        .build()
                    )
                }
            }
        }

    }
}