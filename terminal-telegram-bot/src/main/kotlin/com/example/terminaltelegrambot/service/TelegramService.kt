package com.example.terminaltelegrambot.service

import com.example.terminaltelegrambot.botConfig.MyTelegramPollingBot
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.methods.send.SendDocument
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import java.io.Serializable

@Service
class TelegramService(@Lazy private val telegramLongPollingBot: MyTelegramPollingBot) {
    @Throws(TelegramApiException::class)
    fun executeMessage(message: SendMessage?): Message? {
        return telegramLongPollingBot.execute(message)
    }

    @Throws(TelegramApiException::class)
    fun executeDocument(document: SendDocument?): Message? {
        return telegramLongPollingBot.execute(document)
    }

    @Throws(TelegramApiException::class)
    fun executePhoto(photo: SendPhoto?): Message? {
        return telegramLongPollingBot.execute(photo)
    }

    @Throws(TelegramApiException::class)
    fun executeEditMessageReplyMarkup(editMarkup: EditMessageReplyMarkup?): Serializable? {
        return telegramLongPollingBot.execute(editMarkup)
    }
}