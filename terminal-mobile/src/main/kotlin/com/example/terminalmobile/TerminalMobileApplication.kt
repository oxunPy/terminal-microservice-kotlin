package com.example.terminalmobile

import com.example.common.util.JwtTokenUtil
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
class TerminalMobileApplication{

//    @Value("\${jwt.validity.period}")
//    var JWT_VALIDITY_PERIOD: String? = null
//    @Value("\${jwt.secret.key}")
//    var JWT_SECRET_KEY: String? = null

    @Bean
    fun jwtTokenUtil() = JwtTokenUtil(100000L, "oxun_12121")
//    fun jwtTokenUtil() = JwtTokenUtil(JWT_VALIDITY_PERIOD!!.toLong(), JWT_SECRET_KEY!!)
}

fun main(args: Array<String>) {
    runApplication<TerminalMobileApplication>(*args)
}


