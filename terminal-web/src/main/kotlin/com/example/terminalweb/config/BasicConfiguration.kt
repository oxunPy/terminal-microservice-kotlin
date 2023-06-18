package com.example.terminalweb.config

import com.example.common.util.JwtTokenUtil
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter

@Configuration
class BasicConfiguration {
    @Bean
    fun authenticationManager(): AuthenticationManager? {
        return AuthenticationManager { authentication: Authentication -> if (authentication.isAuthenticated) authentication else null }
    }

    @Bean
    fun basicAuthFilter() = BasicAuthenticationFilter(authenticationManager())

    @Bean
    fun jwtTokenUtil() = JwtTokenUtil(1200L, "oxun_123")
}