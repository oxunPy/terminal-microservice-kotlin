package com.example.terminalweb.config.security

import com.example.terminalweb.repository.UserRepository
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Service
import java.util.*

@Service
class TerminalUserDetailsService(private val userRepository: UserRepository) {

    fun getByUserName(username: String): Optional<TerminalUserDetails>? {
        val userOpt = userRepository.findByLogin(username)
        return userOpt?.map { TerminalUserDetails(it!!.getId()!!, it.getInfo(), it.getLogin(), it.getUser_name(), it.getPass(), Collections.singletonList(SimpleGrantedAuthority(it.getRole()))) }
    }
}