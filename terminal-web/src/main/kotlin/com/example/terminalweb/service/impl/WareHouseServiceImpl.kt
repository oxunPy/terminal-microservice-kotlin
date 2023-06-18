package com.example.terminalweb.service.impl

import com.example.common.forms.WareHouseForm
import com.example.common.forms.table.DataTablesForm
import com.example.common.forms.table.FilterForm
import com.example.common.forms.table.OrderForm
import com.example.terminalweb.repository.WareHouseRepository
import com.example.common.dto.WareHouseDto
import com.example.terminalweb.service.WareHouseService
import org.apache.commons.collections4.MapUtils
import org.apache.commons.lang3.StringUtils
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.stream.Collectors

@Service("WareHouseService")
class WareHouseServiceImpl(private val wareHouseRepository: WareHouseRepository): WareHouseService {
    override fun getAllWareHousesWithName(name: String?): List<WareHouseDto?>? {
        val dtoProjections = wareHouseRepository.getAllWareHousesWithName(name)
        val dtoList = ArrayList<WareHouseDto>()
        if(dtoProjections!!.isNotEmpty()){
            for(projection in dtoProjections){
                dtoList.add(WareHouseDto.buildFromDtoProjection(projection))
            }
        }
        return dtoList
    }

    @Transactional(readOnly = true)
    override fun getWareHouseDataTable(filterForm: FilterForm?): DataTablesForm<WareHouseForm?>? {
        var sort = getSort(filterForm!!.orderMap())

        if(sort.isEmpty){
            sort = Sort.by(Sort.Order(Sort.Direction.ASC, "id"))
        }

        var name: String? = null
        val filterMap = filterForm.filter
        if(filterMap != null){
            name = StringUtils.defaultIfEmpty(MapUtils.getString(filterMap, "name"), "")
        }

        val pageRequest = PageRequest.of(filterForm.start / filterForm.length, filterForm.length, sort)
        val pageWareHouses = wareHouseRepository.getAllWareHousesWithName(name, 0, Integer.MAX_VALUE, pageRequest)
        val wareHouseList = pageWareHouses!!.stream()
            .map { WareHouseForm(it!!.getId(), it.getName(), it.getIsDefault()) }.collect(Collectors.toList())

        val dataTable: DataTablesForm<WareHouseForm?> = DataTablesForm()
        dataTable.data = wareHouseList
        dataTable.draw = filterForm.draw
        dataTable.recordsTotal = pageWareHouses.totalElements.toInt()
        dataTable.recordsFiltered = pageWareHouses.totalElements.toInt()
        return dataTable
    }

    override fun findAllWareHouse(): List<WareHouseForm?>? {
        return wareHouseRepository.getAllWareHousesWithName("")!!.stream()
            .map { WareHouseForm(it!!.getId(), it.getName(), it.getIsDefault()) }.collect(Collectors.toList())
    }

    override fun deleteWarehouse(warehouseId: Long?): Boolean? {
        return wareHouseRepository.deleteWareHouse(warehouseId)
    }

    override fun getWarehouse(warehouseId: Long?): WareHouseForm? {
        return wareHouseRepository.getAllWareHousesWithName("")!!.stream().filter { it!!.getId() == warehouseId }.map { WareHouseForm(it!!.getId(), it.getName(), it.getIsDefault()) }.findFirst().orElse(null)
    }

    override fun saveWarehouse(wareHouseForm: WareHouseForm?): Boolean? {
        return wareHouseRepository.saveWareHouse(wareHouseForm!!.name, wareHouseForm.isDefault == null)
    }

    override fun editWarehouse(wareHouseForm: WareHouseForm?): Boolean? {
        if(wareHouseForm!!.id == null) return false
        return wareHouseRepository.editWareHouse(wareHouseForm.name, wareHouseForm.isDefault != null, wareHouseForm.id)
    }

    private fun getSort(orderMap: Map<String, OrderForm>?): Sort {
        val orders: ArrayList<Sort.Order> = ArrayList()
        orderMap?.forEach{(column, orderForm) ->
            var property: String? = null
            when(column){
                "name" -> property = "name"
                "isDefault" -> property = "isDefault"
            }
            val direction = Sort.Direction.fromString(orderForm.dir!!)
            orders.add(if(direction.isAscending) Sort.Order.asc(property!!) else Sort.Order.desc(property!!))
        }
        return Sort.by(orders)
    }

}