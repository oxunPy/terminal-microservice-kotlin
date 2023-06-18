package com.example.terminalweb.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.LocaleResolver
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.web.servlet.i18n.CookieLocaleResolver
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor
import java.util.*

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = ["com.example.terminalweb.controller"])
class WebConfig: WebMvcConfigurer {
    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry.addResourceHandler(
                                    "/js/**",
                                    "/css/**",
                                    "/flag-icon-css-master/**",
                                    "/excels/**",
                                    "/img/**",
                                    "/media/**",
                                    "/fonts/**",
                                    "/plugins/**"
        ).addResourceLocations(
                                "classpath:/static/js/",
                                "classpath:/static/css/",
                                "classpath:/static/flag-icon-css-master/",
                                "classpath:/static/excels/",
                                "classpath:/static/img/",
                                "classpath:/static/media/",
                                "classpath:/static/fonts/",
                                "classpath:/static/plugins/"
        )
    }

    @Bean
    fun localeResolver(): LocaleResolver? {
        val _uz = Locale("uz")
        val resolver = CookieLocaleResolver()
        resolver.setDefaultLocale(_uz)
        return resolver
    }

    @Bean
    fun localeInterceptor(): LocaleChangeInterceptor? {
        val localeChangeInterceptor = LocaleChangeInterceptor()
        localeChangeInterceptor.paramName = "lang"
        return localeChangeInterceptor
    }

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(localeInterceptor()!!)
    }
}