package com.example.terminalweb.config

import com.example.common.util.JwtTokenUtil
import com.example.terminalweb.config.security.TerminalUserDetails
import com.example.terminalweb.config.security.TerminalUserDetailsService
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class JwtTokenFilter(private val jwtTokenUtil: JwtTokenUtil, private val terminalUserDetailsService: TerminalUserDetailsService): OncePerRequestFilter() {

    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
        val header = request.getHeader(HttpHeaders.AUTHORIZATION)

        if (header == null || header.isEmpty() || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response)
            return
        }

        /*Get jwt token and validate*/
        val token = header.split(" ".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()[1].trim { it <= ' ' }
        if (!jwtTokenUtil.validate(token)) {
            filterChain.doFilter(request, response)
            return
        }
        // Get user identity and set it on the spring security context
        val terminalUserDetails: TerminalUserDetails? =
            terminalUserDetailsService.getByUserName(jwtTokenUtil.getSubjectFromToken(token)!!)!!
                .orElse(null)

        val authentication = UsernamePasswordAuthenticationToken(
            terminalUserDetails,
            null,
            if (terminalUserDetails == null) ArrayList() else terminalUserDetails.authorities
        )

        authentication.details = WebAuthenticationDetailsSource().buildDetails(request)

        SecurityContextHolder.getContext().authentication = authentication
        filterChain.doFilter(request, response)
    }
}