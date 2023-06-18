package com.example.terminaltelegrambot.commands

import org.telegram.telegrambots.meta.exceptions.TelegramApiException

interface CommandHandler<T> {
    @Throws(TelegramApiException::class)
    fun executeCommand(t: T?, command: String?)
}