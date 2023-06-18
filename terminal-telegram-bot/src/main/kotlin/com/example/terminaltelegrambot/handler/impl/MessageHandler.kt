package com.example.terminaltelegrambot.handler.impl

import com.example.terminaltelegrambot.commands.GeneralCommandHandler
import com.example.terminaltelegrambot.handler.Handler
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.Message

@Component
class MessageHandler(private val generalCommandHandler: GeneralCommandHandler, private val contactHandler: ContactHandler): Handler<Message> {
    override fun handleMessage(t: Message) {
        if (t.hasText()) {
            generalCommandHandler.executeCommand(t, t.text)
        }
        if (t.hasContact()) {
            contactHandler.handleMessage(t)
        }
    }
}