package com.example.terminalweb.exception

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

class TerminalWebException(override val message: String): RuntimeException(message)

@RestControllerAdvice
class TerminalExceptionHandler{

    @ExceptionHandler(TerminalWebException::class)
    fun handleMonitoringException(ex: TerminalWebException) = ResponseEntity.badRequest().body(ex.message)
}