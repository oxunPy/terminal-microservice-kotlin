package com.example.terminalweb.service.impl

import com.example.terminalweb.repository.CurrencyRepository
import com.example.common.dto.ExchangeRateDto
import com.example.common.dto.data_interface.ExchangeRateDtoProjection
import com.example.terminalweb.service.CurrencyService
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.util.*

@Service("CurrencyService")
class CurrencyServiceImpl(private val currencyRepository: CurrencyRepository): CurrencyService {
    override fun getLastExchangeRate(): ExchangeRateDto? {
        val lastExchangeRate: Optional<ExchangeRateDtoProjection?>? = currencyRepository.getLastExchangeRate()
        val dto = ExchangeRateDto()
        dto.toCurrencyVal = BigDecimal.valueOf(1)
        dto.mainCurrencyVal = BigDecimal.valueOf(1)
        if(lastExchangeRate!!.isPresent){
            dto.id = lastExchangeRate.get().getId()
            dto.invDate = lastExchangeRate.get().getInv_date()
            dto.mainCurrency = lastExchangeRate.get().getTo_currency()
            dto.toCurrency = lastExchangeRate.get().getTo_currency()
            dto.mainCurrencyVal = lastExchangeRate.get().getMain_currency_val()
            dto.toCurrencyVal = lastExchangeRate.get().getTo_currency_val()
        }
        return dto
    }
}