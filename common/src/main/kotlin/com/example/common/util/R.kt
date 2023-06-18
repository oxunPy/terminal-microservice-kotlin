package com.example.common.util

import org.apache.commons.lang.StringUtils
import java.net.URL
import java.util.*

class R {
    companion object{
        fun getURL(name: String?): URL? {
            return R::class.java.getResource(name)
        }

        fun bundle(locale: Locale?): ResourceBundle? {
            return ResourceBundle.getBundle("messages", locale!!, Utf8Control())
        }

        fun bundle(langCode: String?): ResourceBundle? {
            val locale = Locale(if (StringUtils.isEmpty(langCode)) "ru" else langCode)
            return ResourceBundle.getBundle("messages", locale, Utf8Control())
        }

        fun bundle(): ResourceBundle? {
            return ResourceBundle.getBundle("messages", Locale("ru"), Utf8Control())
        }

        fun getString(locale: Locale?, key: String?): String? {
            return try {
                val bundle: ResourceBundle = ResourceBundle.getBundle("messages", locale!!, Utf8Control())
                bundle.getString(key!!)
            } catch (ex: Exception) {
                throw RuntimeException(String.format("No Such key = %s", key))
            }
        }
    }
}