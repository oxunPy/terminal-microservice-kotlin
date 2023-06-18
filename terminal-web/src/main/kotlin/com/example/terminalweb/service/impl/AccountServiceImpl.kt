package com.example.terminalweb.service.impl

import com.example.common.constants.AccountType
import com.example.common.constants.Status
import com.example.common.forms.AccountForm
import com.example.common.forms.Select2Form
import com.example.common.forms.table.DataTablesForm
import com.example.common.forms.table.FilterForm
import com.example.common.forms.table.OrderForm
import com.example.terminalweb.repository.AccountRepository
import com.example.common.dto.AccountDto
import com.example.common.dto.data_interface.AccountDtoProjection
import com.example.terminalweb.service.AccountService
import org.apache.commons.collections4.MapUtils
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.util.*
import java.util.stream.Collectors
import kotlin.collections.ArrayList

@Service("AccountService")
class AccountServiceImpl(private val accountRepository: AccountRepository): AccountService {
    override fun getAllAccountsWithType(type: Int): List<AccountDto?>? {
        TODO()
    }

    override fun getAllAccountsWithTypeAndNameLike(type: Int, name: String?): List<AccountDto?>? {
       TODO()
    }

    override fun findAllDealerClients(accountType: AccountType?): List<Select2Form?>? {
        val accountDtoProjectionList = accountRepository.getAllAccountsWithType(accountType!!.ordinal, Status.ACTIVE.ordinal, Status.UPDATED.ordinal)
        return accountDtoProjectionList!!.stream().map { Select2Form(it!!.getId()!!.toLong(), it.getPrintable_name()) }.collect(Collectors.toList())
    }

    override fun getAccountDataTable(filterForm: FilterForm?): DataTablesForm<AccountForm?>? {
        var sort: Sort = getSort(filterForm!!.orderMap())

        if(sort.isEmpty){
            sort = Sort.by(Sort.Order.desc("id"))
        }

        var accountType: String? = null;
        val filterMap = filterForm.filter

        if(filterMap != null){
            accountType = MapUtils.getString(filterMap, "accountType")
        }
        val pageRequest: PageRequest = PageRequest.of(filterForm.start / filterForm.length, filterForm.length, sort)
        val pageAccounts = accountRepository.pageAllAccountsWithType(AccountType.valueOf(accountType!!).ordinal, Status.ACTIVE.ordinal, Status.UPDATED.ordinal, pageRequest)
        val accountFormList = pageAccounts!!.stream().map { AccountForm(it!!.getId(), it.getFirst_name(), it.getLast_name(), it.getPrintable_name(), it.getPhone(), it.getOpening_balance()) }.collect(Collectors.toList())

        val datatable = DataTablesForm<AccountForm?>()
        datatable.recordsFiltered = pageAccounts.totalElements.toInt()
        datatable.recordsTotal = pageAccounts.totalElements.toInt()
        datatable.data = accountFormList
        return datatable
    }

    fun getSort(orderMap: Map<String, OrderForm>?): Sort{
        val orders: ArrayList<Sort.Order> = ArrayList();
        orderMap?.forEach{ (column, orderForm) ->
            var property: String? = null
            when(column){
                "printable_name" -> property = "printable_name"
                "firstname" -> property = "firstname"
            }
            val direction = Sort.Direction.fromString(orderForm.dir!!)
            orders.add(if (direction.isAscending) Sort.Order.asc(property!!) else Sort.Order.desc(property!!))
        }
        return Sort.by(orders)
    }
}