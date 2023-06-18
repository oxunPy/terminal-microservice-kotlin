package com.example.terminalmobile.service.impl

import com.example.common.dto.DealerDto
import com.example.common.dto.data_interface.DealerDtoProjection
import com.example.terminalmobile.repository.DealerRepository
import com.example.terminalmobile.service.DealerService
import org.springframework.beans.BeanUtils
import org.springframework.stereotype.Service

@Service("DealerService")
class DealerServiceImpl(private val dealerRepository: DealerRepository): DealerService {
    override fun getDealersWithName(name: String?): List<DealerDto?>? {
        val ddProjections: List<DealerDtoProjection?>? = dealerRepository.getDealersWithNameLike(name)
        val dealerDtos: MutableList<DealerDto> = ArrayList()
        if (ddProjections != null) {
            for (ddProjection in ddProjections) {
                val dto = DealerDto()
                BeanUtils.copyProperties(ddProjection!!, dto)
                dealerDtos.add(dto)
            }
        }
        return dealerDtos
    }
}