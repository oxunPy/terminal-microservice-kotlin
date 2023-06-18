package com.example.terminalweb.util

import javax.servlet.ServletContext
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest

class RequestUtil {
    companion object{
        fun getHead(request: HttpServletRequest): String {
            return String.format("%s://%s:%s", request.getScheme(), request.getServerName(), request.getServerPort())
        }

        fun getRequestPath(request: HttpServletRequest): String? {
            val requestUri: String = request.getRequestURI()
            val contextPath: String = request.getContextPath()
            return requestUri.substring(contextPath.length)
        }

        fun getRequestURI(request: HttpServletRequest, requestPath: String): String? {
            return request.getContextPath() + requestPath
        }

        fun getRequestURL(request: HttpServletRequest, requestPath: String): String? {
            return getHead(request) + request.getContextPath() + requestPath
        }

        fun getServletContext(request: HttpServletRequest): ServletContext? {
            return request.getSession().getServletContext()
        }

        fun getCookieByName(request: HttpServletRequest, name: String): Cookie? {
            return if (request.getCookies() == null) {
                null
            } else {
                for (i in request.getCookies().indices) {
                    if (request.getCookies().get(i).getName() == name) {
                        return request.getCookies().get(i)
                    }
                }
                null
            }
        }

        fun addAttr(request: HttpServletRequest, key: String?, value: Any?) {
            request.getSession().setAttribute(key, value)
        }

        fun removeAttr(request: HttpServletRequest, key: String?) {
            request.getSession().removeAttribute(key)
        }
    }
}