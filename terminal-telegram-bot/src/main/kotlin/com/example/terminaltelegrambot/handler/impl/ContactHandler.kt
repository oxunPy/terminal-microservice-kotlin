package com.example.terminaltelegrambot.handler.impl

import com.example.common.constants.BotState
import com.example.common.constants.Currency
import com.example.common.constants.Status
import com.example.common.dto.BotUserModel
import com.example.terminaltelegrambot.commands.Commands
import com.example.terminaltelegrambot.handler.Handler
import com.example.terminaltelegrambot.service.TelegramService
import com.example.terminaltelegrambot.service.UserService
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Message
import java.time.Instant
import java.util.Date

@Component
class ContactHandler(private val userService: UserService, private val telegramService: TelegramService): Handler<Message> {
    override fun handleMessage(t: Message) {
        val botUserModel = BotUserModel(contact = t.contact.phoneNumber,
                                        userName = t.from.userName,
                                        firstName = t.from.firstName,
                                        lastName = t.from.lastName,
                                        chatId = t.chatId,
                                        botState = BotState.START_STATE,
                                        createdDate = Date.from(Instant.now()),
                                        status = Status.PASSIVE,
                                        currency = Currency.USD,
                                        synced = false
            )
        userService.saveUser(botUserModel)

        telegramService.executeMessage(SendMessage.builder()
                                                  .text("Пользователь успешно создан.\nВаш аккаунт неактивен, обратитесь к администратору!")
            .chatId(t.chatId.toString()).replyMarkup(Commands.getUpdateMarkup()).build())
    }
}