package com.example.terminaltelegrambot.botConfig

import com.example.terminaltelegrambot.handler.impl.UpdateHandler
import javax.annotation.Resource
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.objects.Update

@Component
class MyTelegramPollingBot(): TelegramLongPollingBot() {
    @Value("\${telegram.bot.username}")
    var userName: String? = null
    @Value("\${telegram.bot.token}")
    var token: String? = null

    @Resource
    private lateinit var updateHandler: UpdateHandler

    override fun getBotToken() = token

    override fun getBotUsername() = userName

    override fun onUpdateReceived(update: Update?){
        updateHandler.handleMessage(update!!)
    }
}