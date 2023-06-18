package com.example.terminaltelegrambotapi.service.impl

import com.example.common.constants.Currency
import com.example.common.dto.BotUserModel
import com.example.common.dto.ClientBalance
import com.example.common.dto.data_interface.BalanceInterface
import com.example.terminaltelegrambotapi.repository.RestTelegramBotRepository
import com.example.terminaltelegrambotapi.service.RestTelegramService
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.util.*

@Service("RestTelegramService")
class RestTelegramServiceImpl(private val restTelegramBotRepository: RestTelegramBotRepository): RestTelegramService {
    override fun getReceiptCash(currency: Currency?, fromDate: LocalDate?, toDate: LocalDate?): BigDecimal? {
        val result: Optional<BigDecimal?>? = restTelegramBotRepository.getReceiptCash(currency!!.ordinal + 1, fromDate, toDate)
        if (result!!.isPresent) return formatBigDecimal(result.get(), if (currency.ordinal == 0) 3 else 0)
        return BigDecimal.ZERO
    }

    override fun getReceiptBank(currency: Currency?, fromDate: LocalDate?, toDate: LocalDate?): BigDecimal? {
        val result: Optional<BigDecimal?>? = restTelegramBotRepository.getReceiptBank(currency!!.ordinal + 1, fromDate, toDate)
        if (result!!.isPresent) return formatBigDecimal(result.get(), if (currency.ordinal == 0) 3 else 0)
        return BigDecimal.ZERO
    }

    override fun getPaymentCash(currency: Currency?, fromDate: LocalDate?, toDate: LocalDate?): BigDecimal? {
        val result: Optional<BigDecimal?>? = restTelegramBotRepository.getPaymentCash(currency!!.ordinal + 1, fromDate, toDate)
        if (result!!.isPresent) return formatBigDecimal(result.get(), if (currency.ordinal == 0) 3 else 0)
        return BigDecimal.ZERO
    }

    override fun getPaymentBank(currency: Currency?, fromDate: LocalDate?, toDate: LocalDate?): BigDecimal? {
        val result: Optional<BigDecimal?>? = restTelegramBotRepository.getPaymentBank(currency!!.ordinal + 1, fromDate, toDate)
        if (result!!.isPresent) return formatBigDecimal(result.get(), if (currency.ordinal == 0) 3 else 0)
        return BigDecimal.ZERO
    }

    override fun getTotalReturnedAmountFromClient(currency: Currency?, fromDate: LocalDate?, toDate: LocalDate?): BigDecimal? {
        val result: Optional<BigDecimal?>? = restTelegramBotRepository.getTotalReturnedAmountFromClient(currency!!.ordinal + 1, fromDate, toDate)
        if (result!!.isPresent) return formatBigDecimal(result.get(), if (currency.ordinal == 0) 3 else 0)
        return BigDecimal.ZERO
    }

    override fun getTotalBalanceClient(currency: Currency?, toDate: LocalDate?): ClientBalance? {
        if (currency == null) {
            throw NullPointerException("currency is null")
        }
        val clientBalance = ClientBalance(BigDecimal.valueOf(0.00), BigDecimal.valueOf(0.00))
        val result: Optional<BalanceInterface?>? = restTelegramBotRepository.getTotalBalanceClient(currency.ordinal + 1, toDate)
        if (result!!.isPresent) {
            clientBalance.credit =
                                    if (result.get().getCredit() != null) formatBigDecimal(result.get().getCredit()!!,if (currency.ordinal == 0) 3 else 0)
                                    else formatBigDecimal(BigDecimal.valueOf(0.00), if (currency.ordinal == 0) 3 else 0)
            clientBalance.debit =
                                    if (result.get().getDebit() != null) formatBigDecimal(result.get().getDebit()!!,if (currency.ordinal == 0) 3 else 0)
                                    else formatBigDecimal(BigDecimal.valueOf(0.00), if (currency.ordinal == 0) 3 else 0)

        }
        return clientBalance
    }

    override fun saveBotUserModel(bum: BotUserModel): BotUserModel? {
        restTelegramBotRepository.save(
                                        bum.userName, bum.chatId,
                                        bum.status!!.ordinal, bum.contact,
                                        bum.firstName, bum.lastName,
                                        bum.botState.name, bum.currency!!.name, bum.synced,
                                        bum.createdDate, bum.command
        )
        return bum
    }

    override fun userExists(chatId: Long) = restTelegramBotRepository.existsUserByChatId(chatId)

    override fun getBotUser(chatId: Long): BotUserModel? {
        val optBotUserInterface = restTelegramBotRepository.getBotUserByChatId(chatId)
        return optBotUserInterface?.map { BotUserModel(it!!.getId(), it.getChatId(), it.getUserName(), it.getFirstName(), it.getLastName(), it.getContact(), it.getStatus(), it.getCommand(), it.getBotState()!!, it.getCurrency()) }!!.get()
    }

    override fun activateUser(chatId: Long) = restTelegramBotRepository.activateUser(chatId) > 0

    private fun withBigDecimal(value: Double, places: Int): Double {
        var bigDecimal = BigDecimal(value)
        bigDecimal = bigDecimal.setScale(places, RoundingMode.HALF_UP)
        return bigDecimal.toDouble()
    }

    private fun formatBigDecimal(value: BigDecimal, places: Int): BigDecimal? {
        return value.setScale(places, RoundingMode.HALF_UP)
    }
}