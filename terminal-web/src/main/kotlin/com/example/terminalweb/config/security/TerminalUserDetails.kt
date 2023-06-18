package com.example.terminalweb.config.security

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.User

class TerminalUserDetails(username: String?, password: String?, authorities: MutableCollection<out GrantedAuthority>?) : User(username, password, authorities) {
    var id: Long? = null
    var info: String? = null
    var login: String? = null

    constructor(id: Long, info: String?, login: String?, username: String?, password: String?, authorities: MutableCollection<out GrantedAuthority>?) : this(username, password, authorities) {
        this.id = id
        this.info = info
        this.login = login
    }
}