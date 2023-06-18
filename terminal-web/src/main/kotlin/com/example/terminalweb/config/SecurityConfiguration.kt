package com.example.terminalweb.config

import com.example.terminalweb.config.security.AccessChecker
import com.example.terminalweb.config.security.AuthenticationHandler
import com.example.terminalweb.config.security.TerminalUserDetailsService
import com.example.terminalweb.util.Encryption
import org.springframework.context.annotation.Bean
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@EnableWebSecurity
@EnableGlobalMethodSecurity(
    securedEnabled = true,
    jsr250Enabled = true,
    prePostEnabled = true
)
class SecurityConfiguration(private val terminalUserDetailsService: TerminalUserDetailsService, private val jwtTokenFilter: JwtTokenFilter): WebSecurityConfigurerAdapter() {
    companion object{
        private var NOT_FILTER = arrayOf(
                                            "/api/user/login/**",
                                            "/api/test**",
                                            "/api-docs", "/api-docs/*", "/swagger-ui", "/swagger-ui/*",
                                            "/",
                                            "/img/**",
                                            "/login",
                                            "/login.do",
                                            "/generateQRCode",
                                            "/company/favicon",
                                            "/company/logo",
                                            "/data/i18n"
        )
    }

    override fun configure(auth: AuthenticationManagerBuilder?) {
        auth!!.userDetailsService(UserDetailsService { username: String? ->terminalUserDetailsService.getByUserName(username!!)!!.get()}).passwordEncoder(passwordEncoder())
    }

    override fun configure(web: WebSecurity?) {
        web!!
            .ignoring()
            .antMatchers("/css/**", "/js/**", "/media/**", "/fonts/**", "/excel/**")
    }

    override fun configure(http: HttpSecurity?) {
        //enable CORS and disable CSRF
        http!!.cors().and().csrf().disable()

        // Set unauthorized requests exception handler
        http
            .exceptionHandling()
            .authenticationEntryPoint { request: HttpServletRequest?, response: HttpServletResponse, ex: AuthenticationException ->
                response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    ex.message
                )
            }
            .and()

        // Set permissions on endpoints
        http.authorizeRequests() // Our public endpoints
            .antMatchers(*NOT_FILTER).permitAll() // Our private endpoints
            .antMatchers("api/v1/**").hasAnyRole("Administrator")
            .and()
                .authorizeRequests().anyRequest().access("@accessChecker.check(authentication, request)")
            .and()
                .formLogin()
                .loginPage("/login.do")
                .usernameParameter("username").passwordParameter("password").permitAll()
                .loginProcessingUrl("/login")
                .successHandler(authenticationHandler())
                .failureForwardUrl("/login.do?error=true")
            .and()
                .logout()
                .logoutUrl("/logout.do")
                .logoutSuccessUrl("/login.do?logout=true")
            .and()
                .exceptionHandling()
                .accessDeniedPage("/login.do?denied=true")

        // Add JWT token filter
        http.addFilterAfter(jwtTokenFilter, UsernamePasswordAuthenticationFilter::class.java)
    }

    @Bean
    fun passwordEncoder() = Encryption()

    @Bean
    fun authenticationHandler() = AuthenticationHandler()

    @Bean("accessChecker")
    fun accessChecker() = AccessChecker()

    @Bean
    fun corsFilter(): CorsFilter{
        val source = UrlBasedCorsConfigurationSource()
        val config = CorsConfiguration()
        config.allowCredentials = true
        config.addAllowedOrigin("*")
        config.addAllowedHeader("*")
        config.addAllowedMethod("*")
        source.registerCorsConfiguration("/**", config)
        return CorsFilter(source)
    }
}