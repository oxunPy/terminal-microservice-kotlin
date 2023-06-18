package com.example.terminalmobile.service.impl

import com.example.common.constants.Currency
import com.example.common.dto.ExchangeRateDto
import com.example.common.dto.ProductDto
import com.example.common.dto.data_interface.ProductDtoProjection
import com.example.terminalmobile.repository.ProductRepository
import com.example.terminalmobile.service.CurrencyService
import com.example.terminalmobile.service.ProductService
import org.apache.commons.lang.StringUtils
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.util.*
import java.util.stream.Collectors

@Service("ProductService")
class ProductServiceImpl(private val productRepository: ProductRepository, private val currencyService: CurrencyService): ProductService {
    override fun findProductListByBarcode(barcode: String?): List<ProductDto?>? {
        if(StringUtils.isBlank(barcode)) throw IllegalStateException("barcode is invalid!!!")
        val lastExchangeRateDto = currencyService.getLastExchangeRate()
        val pBarcodeList = productRepository.getProductListByBarcode(barcode)
        return pBarcodeList?.stream()?.map { ProductDto(it!!.getId(), it.getProduct_name(), getProductRate(it, lastExchangeRateDto!!), it.getRate(), it.getCurrency(),it.getGroup_name()) }!!.collect(
            Collectors.toList())
    }

    override fun findProductByNameLike(productName: String?): List<ProductDto?>? {
        val lastExchangeRateDto = currencyService.getLastExchangeRate()
        var param1: String? = null
        var param2: String? = null
        var param3: String? = null
        var param4: String? = null
        val params = productName!!.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        param1 = params[0]
        param2 = if (params.size > 1 && params[1].isNotEmpty()) params[1] else params[0]
        param3 = if (params.size > 2 && params[2].isNotEmpty()) params.get(2) else params[0]
        param4 = if (params.size > 3 && params[3].isNotEmpty()) params.get(3) else params[0]

        val searchedProductList = productRepository.getProductListByNameLike(param1, param2, param3, param4)
        return searchedProductList?.stream()?.map { ProductDto(it!!.getId(), it.getProduct_name(), it.getRate(), getProductRate(it, lastExchangeRateDto!!), it.getCurrency(), it.getGroup_name()) }!!.collect(Collectors.toList())
    }

    override fun getProductOstatokById(productId: Int?, warehouseId: Int?): BigDecimal? {
        return productRepository.getProductOstatokByProductId(productId!!, warehouseId!!, Date())
    }

    private fun getProductRate(searchedProduct: ProductDtoProjection, lastExchangeRate: ExchangeRateDto): BigDecimal? {
        if(searchedProduct.getCurrency() == null || searchedProduct.getCurrency().equals(Currency.USD.name) && lastExchangeRate.mainCurrency != null &&
            lastExchangeRate.mainCurrency.equals(Currency.USD.name)){
            return getRateUZS(lastExchangeRate, (if(searchedProduct.getRate() != null) searchedProduct.getRate() else BigDecimal.ZERO))
        }
        return searchedProduct.getRate()
    }

    private fun getRateUZS(exchangeRateDto: ExchangeRateDto, productRate: BigDecimal?): BigDecimal? {
        return if (!exchangeRateDto.mainCurrency.equals(Currency.UZS.name)) {
            productRate!!.multiply(exchangeRateDto.toCurrencyVal)
                .divide(exchangeRateDto.mainCurrencyVal)
        } else productRate
    }
}