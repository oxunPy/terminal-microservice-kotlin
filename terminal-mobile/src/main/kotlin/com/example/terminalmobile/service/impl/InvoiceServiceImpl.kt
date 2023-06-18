package com.example.terminalmobile.service.impl


import com.example.common.constants.*
import com.example.common.dto.ExchangeRateDto
import com.example.common.dto.InvoiceDto
import com.example.common.dto.InvoiceItemDto
import com.example.common.dto.PagedResponseDto
import com.example.common.dto.data_interface.InvoiceDtoProjection
import com.example.common.dto.data_interface.InvoiceItemDtoProjection
import com.example.common.dto.data_interface.PagedResponseDtoProjection
import com.example.common.util.DateUtils
import com.example.terminalmobile.repository.InvoiceRepository
import com.example.terminalmobile.service.CurrencyService
import com.example.terminalmobile.service.InvoiceService
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.util.function.Consumer
import java.util.stream.Collectors

@Service("InvoiceService")
class InvoiceServiceImpl(private val invoiceRepository: InvoiceRepository, private val currencyService: CurrencyService): InvoiceService {
    override fun create(source: Int, warehouseId: Int, invoiceDto: InvoiceDto?, uid: String?): Long? {
        val typeSource: String = if (source == 0) TypeSource.DESKTOP.name else TypeSource.MOBILE.name

        val invoiceId = invoiceRepository.createInvoice(
            invoiceDto!!.accountId,
            DateUtils.stringToLocalDateFormat(invoiceDto.date),
            invoiceDto.type,
            invoiceDto.info,
            typeSource,
            Status.TO_PAY.ordinal, uid
        )

        val actionType = if (invoiceDto.type == 0) 1 else -1
        val lastExchangeRate: ExchangeRateDto? = currencyService.getLastExchangeRate()

        val invoiceItemDtoList: List<InvoiceItemDto> = invoiceDto.invoiceItemDtoList!!

        invoiceItemDtoList.forEach(Consumer<InvoiceItemDto> { invoiceItemDto: InvoiceItemDto ->
            invoiceItemDto.invoiceId = invoiceId
            val invoiceItemId = invoiceRepository.createInvoiceItem(
                invoiceItemDto.invoiceId,
                invoiceItemDto.productId,
                invoiceItemDto.quantity,
                actionType,
                Status.ACTIVE.ordinal
            )
            createInvoiceItemAmount(
                invoiceItemId!!,
                invoiceItemDto.quantity!!.multiply(invoiceItemDto.sellingRate),
                invoiceItemDto.sellingRate!!,
                invoiceItemDto.originalRate!!.multiply(lastExchangeRate!!.toCurrencyVal),
                Currency.UZS.ordinal + 1,
                BigDecimal.valueOf(1),
                BigDecimal.valueOf(1),
                InvoiceItemAmountType.MAIN.ordinal,
                Status.ACTIVE.ordinal
            )
            createInvoiceItemAmount(
                invoiceItemId,
                invoiceItemDto.quantity!!.multiply(invoiceItemDto.originalRate!!),
                invoiceItemDto.originalRate!!,
                null,
                Currency.USD.ordinal + 1,
                BigDecimal.valueOf(1),
                BigDecimal.valueOf(1),
                InvoiceItemAmountType.STANDART.ordinal,
                Status.ACTIVE.ordinal
            )
            createInvoiceItemAmount(
                invoiceItemId,
                invoiceItemDto.quantity!!.multiply(invoiceItemDto.sellingRate!!)
                    .divide(lastExchangeRate.toCurrencyVal, 4, RoundingMode.HALF_UP),
                invoiceItemDto.sellingRate!!
                    .divide(lastExchangeRate.toCurrencyVal, 4, RoundingMode.HALF_UP),
                invoiceItemDto.originalRate,
                Currency.USD.ordinal + 1,
                BigDecimal.valueOf(1),
                lastExchangeRate.toCurrencyVal!!,
                InvoiceItemAmountType.OTHER.ordinal,
                Status.ACTIVE.ordinal
            )
        })

        return invoiceId
    }

    override fun getAll(type: Int, warehouseId: Int, source: Int, page: Int, size: Int, fromDate: LocalDate?, toDate: LocalDate?): PagedResponseDto<InvoiceDto?>? {
        val types = if (type == -1) InvoiceType.PRIXOD_BAZA.ordinal.toString() + "," + InvoiceType.RASXOD_KLIENT.ordinal else type.toString()

        val typeSource: String = if (source == 0) TypeSource.DESKTOP.name else TypeSource.MOBILE.name
        var invoiceDtoProjectionList: List<InvoiceDtoProjection?>? =
            if (type == 5) invoiceRepository.getInvoicesGoDown(typeSource, warehouseId, size, (page - 1) * size, fromDate, toDate)
            else invoiceRepository.getInvoices(types, warehouseId, typeSource, size, (page - 1) * size, fromDate, toDate, Status.ACTIVE.ordinal, Status.UPDATED.ordinal, Status.TO_PAY.ordinal)

        val invoiceDtoList: MutableList<InvoiceDto> = invoiceDtoProjectionList!!.stream()
            .map{ invoiceDtoProjection: InvoiceDtoProjection? ->
                InvoiceDto(
                    invoiceDtoProjection!!.getId(),
                    invoiceDtoProjection.getAccount_id(),
                    invoiceDtoProjection.getAccount_printable_name(),
                    invoiceDtoProjection.getDate(),
                    invoiceDtoProjection.getType()!!,
                    invoiceDtoProjection.getInfo(),
                    invoiceDtoProjection.getStatus(),
                    invoiceDtoProjection.getCount_of_items(),
                    invoiceDtoProjection.getTotal(),
                    null
                )
            }.collect(Collectors.toList())
        val invoicesCountAndSum: PagedResponseDtoProjection? = invoiceRepository.getInvoicesCountAndSum(types, typeSource, fromDate, toDate, Status.ACTIVE.ordinal, Status.UPDATED.ordinal, Status.TO_PAY.ordinal)
        return PagedResponseDto(invoiceDtoList, invoicesCountAndSum!!.getCount(), invoicesCountAndSum.getSum())
    }

    override fun getAllItemsById(id: Long?): List<InvoiceItemDto?>? {
        val invoiceItemDtoProjectionList: List<InvoiceItemDtoProjection?>? =
            invoiceRepository.getInvoiceItemsByInvoiceId(id, Status.ACTIVE.ordinal, Status.UPDATED.ordinal, Status.TO_PAY.ordinal)

        return invoiceItemDtoProjectionList!!.stream()
            .map { invoiceItemDtoProjection: InvoiceItemDtoProjection? ->
                InvoiceItemDto(
                    invoiceItemDtoProjection!!.getId(),
                    null,
                    invoiceItemDtoProjection.getProduct_id(),
                    invoiceItemDtoProjection.getProduct_name(),
                    invoiceItemDtoProjection.getQuantity(),
                    null,
                    invoiceItemDtoProjection.getSelling_rate(),
                    null,
                    invoiceItemDtoProjection.getCurrencyCode(),
                    invoiceItemDtoProjection.getTotal()
                )
            }
            .collect(Collectors.toList())
    }

    override fun getInvoiceUid(uid: String?): InvoiceDto? {
        val projection = invoiceRepository.getInvoiceByUid(uid)
        return if (projection!!.isNotEmpty()) {
            InvoiceDto(
                projection[0]!!.getId(),
                projection[0]!!.getAccount_id(),
                projection[0]!!.getAccount_printable_name(),
                projection[0]!!.getDate(),
                projection[0]!!.getType()!!,
                projection[0]!!.getInfo(),
                projection[0]!!.getStatus(),
                projection[0]!!.getCount_of_items(),
                projection[0]!!.getTotal(),
                null
            )
        } else null
    }

    override fun update(source: Int, warehouseId: Int, invoiceDto: InvoiceDto?, uid: String?): Long? {
        val dto = getInvoiceUid(uid)
        val id: Long

        if (dto != null) {
            val lastExchangeRate = currencyService.getLastExchangeRate()
            val actionType = if (invoiceDto!!.type == 0) 1 else -1
            val typeSource: String = if (source == 0) TypeSource.DESKTOP.name else TypeSource.MOBILE.name
            id = dto.id!!

            // update invoice and change status to invoice_item and invoice_item_amount
            invoiceRepository.updateInvoice(invoiceDto!!.accountId!!.toInt(), warehouseId, invoiceDto.type, invoiceDto.info, typeSource, Status.TO_PAY.ordinal, dto.id!!.toInt(), DateUtils.stringToLocalDateFormat(invoiceDto.date))
            invoiceRepository.changeInvoiceItemStatus(dto.id, Status.DELETED.ordinal)
            invoiceRepository.getInvoiceItemsByInvoiceId(id, Status.ACTIVE.ordinal, Status.UPDATED.ordinal, Status.TO_PAY.ordinal)
                ?.forEach { invoiceItemDtoProjection ->
                    invoiceRepository.changeInvoiceItemAmountStatus(
                        invoiceItemDtoProjection!!.getId(),
                        Status.DELETED.ordinal
                    )
                }

            // create new invoice_item and new invoice_item_amount
            val invoiceItemDtoList: List<InvoiceItemDto> = invoiceDto.invoiceItemDtoList!!
            invoiceItemDtoList.forEach(Consumer<InvoiceItemDto> { invoiceItemDto: InvoiceItemDto ->
                invoiceItemDto.invoiceId = id
                val invoiceItemId = invoiceRepository.createInvoiceItem(invoiceItemDto.invoiceId, invoiceItemDto.productId, invoiceItemDto.quantity, actionType, Status.ACTIVE.ordinal)
                createInvoiceItemAmount(invoiceItemId!!,
                    invoiceItemDto.quantity!!.multiply(invoiceItemDto.sellingRate),
                    invoiceItemDto.sellingRate!!,
                    invoiceItemDto.sellingRate!!.multiply(lastExchangeRate!!.toCurrencyVal),
                    Currency.UZS.ordinal + 1,
                    BigDecimal.valueOf(1),
                    BigDecimal.valueOf(1),
                    InvoiceItemAmountType.MAIN.ordinal,
                    Status.ACTIVE.ordinal
                )
                createInvoiceItemAmount(
                    invoiceItemId,
                    invoiceItemDto.quantity!!.multiply(invoiceItemDto.originalRate),
                    invoiceItemDto.originalRate!!,
                    null,
                    Currency.USD.ordinal + 1,
                    BigDecimal.valueOf(1),
                    BigDecimal.valueOf(1),
                    InvoiceItemAmountType.STANDART.ordinal,
                    Status.ACTIVE.ordinal
                )
                createInvoiceItemAmount(
                    invoiceItemId,
                    invoiceItemDto.quantity!!.multiply(invoiceItemDto.sellingRate)
                        .divide(lastExchangeRate!!.toCurrencyVal, 4, RoundingMode.HALF_UP),
                    invoiceItemDto.sellingRate!!
                        .divide(lastExchangeRate.toCurrencyVal, 4, RoundingMode.HALF_UP),
                    invoiceItemDto.originalRate!!,
                    Currency.USD.ordinal + 1,
                    BigDecimal.valueOf(1),
                    lastExchangeRate.toCurrencyVal!!,
                    InvoiceItemAmountType.OTHER.ordinal,
                    Status.ACTIVE.ordinal
                )
            })
            return id
        }
        return null
    }


    private fun createInvoiceItemAmount(
        invoiceItemId: Long, amount: BigDecimal, rate: BigDecimal, originalRate: BigDecimal?,
        currencyId: Int, convertion: BigDecimal, denominator: BigDecimal, type: Int, status: Int
    ) = invoiceRepository.createInvoiceItemAmount(invoiceItemId, amount, rate, originalRate, currencyId, convertion,denominator, type, status)

}