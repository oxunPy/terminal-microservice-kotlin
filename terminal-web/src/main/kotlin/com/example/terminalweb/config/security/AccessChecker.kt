package com.example.terminalweb.config.security

import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import javax.servlet.http.HttpServletRequest

class AccessChecker {
    fun check(authentication: Authentication, request: HttpServletRequest): Boolean{
        return check(authentication, request.requestURI)
    }

    fun check(authentication: Authentication, uri: String): Boolean{
        if(authentication.principal != null && authentication.principal is TerminalUserDetails){
            val terminalUserDetails = authentication.principal as TerminalUserDetails
            return terminalUserDetails.authorities.contains(SimpleGrantedAuthority("Administrator"))
        }
        return false
    }
}