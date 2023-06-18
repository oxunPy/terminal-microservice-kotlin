package com.example.terminalweb

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient

@SpringBootApplication
@EnableDiscoveryClient
class TerminalWebApplication

fun main(args: Array<String>) {
    runApplication<TerminalWebApplication>(*args)
}
