package com.example.terminaltelegrambotapi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient

@SpringBootApplication
class TerminalTelegramBotApiApplication

fun main(args: Array<String>) {
    runApplication<TerminalTelegramBotApiApplication>(*args)
}