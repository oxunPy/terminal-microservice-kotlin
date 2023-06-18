package com.example.terminalweb.util

import com.example.terminalweb.config.security.TerminalUserDetails
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder

class BackendSecurityUtils {
    companion object{
        fun getUserId(): Long? {
            if(SecurityContextHolder.getContext().authentication is UsernamePasswordAuthenticationToken){
                val token = SecurityContextHolder.getContext().authentication as UsernamePasswordAuthenticationToken
                if(token.principal is TerminalUserDetails){
                    return (token.principal as TerminalUserDetails).id
                }
            }
            return null
        }

        fun getUserDetails(): TerminalUserDetails? {
            if(SecurityContextHolder.getContext().authentication is UsernamePasswordAuthenticationToken){
                val token =SecurityContextHolder.getContext().authentication as UsernamePasswordAuthenticationToken
                if(token.principal is TerminalUserDetails){
                    return token.principal as TerminalUserDetails
                }
            }
            return null
        }
    }
}