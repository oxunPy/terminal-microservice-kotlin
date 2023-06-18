package com.example.terminaltelegrambot.handler

import org.telegram.telegrambots.meta.exceptions.TelegramApiException

interface Handler<T> {
    @Throws(TelegramApiException::class)
    fun handleMessage(t: T)
}