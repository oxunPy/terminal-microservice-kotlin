package com.example.terminalweb.service.impl

import com.example.common.forms.DealerForm
import com.example.common.forms.Select2Form
import com.example.common.forms.table.DataTablesForm
import com.example.common.forms.table.FilterForm
import com.example.common.forms.table.OrderForm
import com.example.terminalweb.repository.DealerRepository
import com.example.common.dto.DealerDto
import com.example.common.dto.data_interface.DealerDtoProjection
import com.example.terminalweb.service.DealerService
import org.apache.commons.collections4.MapUtils
import org.springframework.beans.BeanUtils
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.util.stream.Collectors

@Service("DealerService")
class DealerServiceImpl(private val dealerRepository: DealerRepository): DealerService {
    override fun getDealersDataTable(filter: FilterForm?): DataTablesForm<DealerForm?>? {
        var sort = getSort(filter!!.orderMap()!!)
        if(sort.isEmpty){
            sort = Sort.by(Sort.Direction.DESC, "id")
        }
        val pageRequest = PageRequest.of(filter!!.start / filter.length, filter.length, sort)
        val filterMap: Map<String, Any>? = filter.filter

        var name: String? = null
        if(filterMap != null){
            name = MapUtils.getString(filterMap, "name")
        }

        val pageDealers: Page<DealerDtoProjection?>? = dealerRepository.pageDealers(name, pageRequest)

        val dealerList: List<DealerForm> = pageDealers!!.stream().map { DealerForm(it!!.getId(), it.getName(), it.getDealerCode()) }.collect(Collectors.toList())

        val dataTableForm: DataTablesForm<DealerForm?> = DataTablesForm()
        dataTableForm.data = dealerList
        dataTableForm.draw = filter.draw
        dataTableForm.recordsTotal = pageDealers.totalElements.toInt()
        dataTableForm.recordsFiltered = pageDealers.totalElements.toInt()
        return dataTableForm
    }

    override fun findAllDealer(): List<Select2Form?>? {
        return dealerRepository.getDealersWithNameLike("")!!.stream().map { Select2Form(it!!.getId(), it.getName()) }.collect(Collectors.toList())
    }

    private fun count(name: String) = dealerRepository.count(name)

    private fun getSort(orderFormMap: Map<String, OrderForm>): Sort{
        val orders: ArrayList<Sort.Order> = ArrayList()
        orderFormMap.forEach{ (column, orderForm) ->
            var property: String? = null
            when(column){
                "name" -> property = "name"
                "dealerCode" -> property = "dealerCode"
            }
            val direction: Sort.Direction = Sort.Direction.fromString(orderForm.dir!!)
            orders.add(if(direction.isAscending) Sort.Order.asc(property!!) else Sort.Order.desc(property!!))
        }
        return Sort.by(orders)
    }
}