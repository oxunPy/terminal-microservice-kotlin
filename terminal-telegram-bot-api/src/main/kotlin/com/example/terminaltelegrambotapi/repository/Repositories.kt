package com.example.terminaltelegrambotapi.repository

import com.example.common.dto.data_interface.BalanceInterface
import com.example.common.dto.data_interface.UserInterface
import com.example.terminaltelegrambotapi.entity.RootEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*

interface RestTelegramBotRepository: JpaRepository<RootEntity, Long> {
    @Query(nativeQuery = true, value = "select * from get_receipt_cash(:currency_id, :from_date, :to_date)")
    fun getReceiptCash(
        @Param("currency_id") currencyId: Int?,
        @Param("from_date") fromDate: LocalDate?,
        @Param("to_date") toDate: LocalDate?
    ): Optional<BigDecimal?>?


    @Query(nativeQuery = true, value = "select * from get_payment_cash(:currency_id, :from_date, :to_date)")
    fun getPaymentCash(
        @Param("currency_id") currencyId: Int?,
        @Param("from_date") fromDate: LocalDate?,
        @Param("to_date") toDate: LocalDate?
    ): Optional<BigDecimal?>?

    @Query(nativeQuery = true, value = "select * from get_receipt_bank(:currency_id, :from_date, :to_date)")
    fun getReceiptBank(
        @Param("currency_id") currencyId: Int?,
        @Param("from_date") fromDate: LocalDate?,
        @Param("to_date") toDate: LocalDate?
    ): Optional<BigDecimal?>?

    @Query(nativeQuery = true, value = "select * from get_payment_bank(:currency_id, :from_date, :to_date)")
    fun getPaymentBank(
        @Param("currency_id") currencyId: Int?,
        @Param("from_date") fromDate: LocalDate?,
        @Param("to_date") toDate: LocalDate?
    ): Optional<BigDecimal?>?

    @Query(
        nativeQuery = true,
        value = "select * from get_total_returned_amount_from_client(:currency_id, :from_date, :to_date)"
    )
    fun getTotalReturnedAmountFromClient(
        @Param("currency_id") currencyId: Int?,
        @Param("from_date") fromDate: LocalDate?,
        @Param("to_date") toDate: LocalDate?
    ): Optional<BigDecimal?>?

    @Query(nativeQuery = true, value = "select * from get_total_balance_client(:currency_id, :to_date)")
    fun getTotalBalanceClient(
        @Param("currency_id") currencyId: Int?,
        @Param("to_date") toDate: LocalDate?
    ): Optional<BalanceInterface?>?

    @Query(
        nativeQuery = true, value = """SELECT
         save_bot_user(:user_name, :chat_id, :status, :contact,
        :first_name, :last_name, :bot_state, :currency, :synced, :created , :command);"""
    )
    fun save(
        @Param("user_name") userName: String?,
        @Param("chat_id") chatId: Long?,
        @Param("status") status: Int,
        @Param("contact") contact: String?,
        @Param("first_name") firstName: String?,
        @Param("last_name") lastName: String?,
        @Param("bot_state") botState: String?,
        @Param("currency") currency: String?,
        @Param("synced") synced: Boolean?,
        @Param("created") created: Date?,
        @Param("command") command: String?
    ): Int

    @Query(
        nativeQuery = true, value = "select  " +
                "id as id, status as status, bot_state as botState, chat_id as chatId, \n" +
                "first_name as firstName, last_name as lastName, user_name as userName, \n" +
                "command as command, contact as contact, currency as currency \n" +
                "from bot_users \n" +
                "where chat_id = :chat_id"
    )
    fun getBotUserByChatId(@Param("chat_id") chatId: Long?): Optional<UserInterface?>?


    @Query(value = "SELECT EXISTS(SELECT 1 FROM bot_users WHERE chat_id = :chat_id)", nativeQuery = true)
    fun existsUserByChatId(@Param("chat_id") chatId: Long?): Boolean

    @Modifying
    @Query(nativeQuery = true, value = "UPDATE bot_users SET STATUS = 1 WHERE chat_id = :chat_id\n")
    fun activateUser(@Param("chat_id") chatId: Long?): Int
}