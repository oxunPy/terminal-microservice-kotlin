package com.example.terminalmobile.service.impl

import com.example.common.constants.Status
import com.example.common.dto.AccountDto
import com.example.common.dto.data_interface.AccountDtoProjection
import com.example.terminalmobile.repository.AccountRepository
import com.example.terminalmobile.service.AccountService
import org.springframework.stereotype.Service
import java.util.stream.Collectors

@Service("AccountService")
class AccountServiceImpl(private val accountRepository: AccountRepository): AccountService {
    override fun getAllAccountsWithType(type: Int): List<AccountDto?>? {
        val accountDtoProjectionList: List<AccountDtoProjection?>? = accountRepository.getAllAccountsWithType(type, Status.ACTIVE.ordinal, Status.UPDATED.ordinal)
        return accountDtoProjectionList!!.stream()
            .map { AccountDto(it!!.getId(), it.getFirst_name(), it.getLast_name(), it.getPrintable_name(), it.getPhone()) }
            .collect(Collectors.toList())
    }

    override fun getAllAccountsWithTypeAndNameLike(type: Int, name: String?): List<AccountDto?>? {
        val accountDtoProjectionList = accountRepository.getAllAccountsWithTypeAndNameLike(type, name, Status.ACTIVE.ordinal, Status.UPDATED.ordinal)
        return accountDtoProjectionList!!.stream()
            .map { AccountDto(it!!.getId(), it.getFirst_name(), it.getLast_name(), it.getPrintable_name(), it.getPhone()) }
            .collect(Collectors.toList())
    }
}