package com.example.terminalmobile.service.impl

import com.example.common.dto.UserDto
import com.example.common.util.Encoder
import com.example.terminalmobile.repository.UserRepository
import com.example.terminalmobile.service.UserService
import org.springframework.stereotype.Service

@Service("UserService")
class UserServiceImpl(private val userRepository: UserRepository): UserService {
    override fun login(login: String?, password: String?): UserDto? {
        val passwordEncoded = Encoder.convert(login!!)

        val loggedUser = userRepository.getUser(login, passwordEncoded)
        if(loggedUser!!.isPresent){
            val userDto = UserDto()
            userDto.login = loggedUser.get().getLogin()
            userDto.userName = loggedUser.get().getUser_name()
            userDto.password = password
            return userDto
        }
        return null
    }
}