package com.example.terminaltelegrambot.handler.impl

import com.example.terminaltelegrambot.handler.Handler
import com.example.terminaltelegrambot.service.TelegramService
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update

@Component
class UpdateHandler(private val telegramService: TelegramService, private val messageHandler: MessageHandler, private val callBackQueryHandler: CallBackQueryHandler): Handler<Update> {
    override fun handleMessage(update: Update) {
        if (update.hasMessage()) {
            messageHandler.handleMessage(update.message)
        } else if (update.hasCallbackQuery()) {
            callBackQueryHandler.handleMessage(update.callbackQuery)
        } else {
            telegramService.executeMessage(
                SendMessage.builder()
                    .chatId(update.message.chatId.toString())
                    .text(String.format("reply to message %s", update.message))
                    .replyToMessageId(update.message.messageId)
                    .build()
            )
        }
    }
}