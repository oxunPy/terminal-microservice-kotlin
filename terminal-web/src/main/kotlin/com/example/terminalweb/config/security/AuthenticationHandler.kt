package com.example.terminalweb.config.security

import com.example.terminalweb.util.BackendSecurityUtils
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class AuthenticationHandler: AuthenticationSuccessHandler {
    override fun onAuthenticationSuccess(request: HttpServletRequest?, response: HttpServletResponse?, authentication: Authentication) {
        val terminalUserDetails = BackendSecurityUtils.getUserDetails()
        if(terminalUserDetails != null){
            return response!!.sendRedirect("/page/companies")
        }
    }
}