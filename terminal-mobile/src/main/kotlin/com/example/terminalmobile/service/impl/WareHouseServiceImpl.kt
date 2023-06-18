package com.example.terminalmobile.service.impl

import com.example.common.dto.WareHouseDto
import com.example.terminalmobile.repository.WareHouseRepository
import com.example.terminalmobile.service.WareHouseService
import org.springframework.stereotype.Service

@Service("WareHouse")
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
}