package com.example.common.constants

import java.util.Locale

enum class Enums(parent: Enums?) {
    START_STATE(null),

    CURRENCY_STATE(START_STATE),

    DATE_STATE(CURRENCY_STATE),

    CALENDAR_STATE(DATE_STATE);

    private val parentState: Enums? = parent
}

enum class Currency{
    USD, UZS
}

enum class AccountType {
    BANK, CASH, CREDITOR, DEBITOR, EXPENSE
}

enum class IMAGETYPE{
    LOGO, FAVICON
}

enum class InvoiceItemAmountType{
    MAIN, STANDART, OTHER
}

enum class InvoiceType(
    private val englishText: String,
    private val russianText: String,
    private val uzbekText: String
) {
    PRIXOD_BAZA("COMING_BASE", "ПРИХОД_БАЗА", "PRIXOD_BAZA"),

    VOZVRAT_BAZA("RETURN_BASE", "ВОЗВРАТ_БАЗА", "VOZVRAT_BAZA"),

    RASXOD_KLIENT("CONSUMPTION_CLIENT", "РАСХОД_КЛИЕНТ", "RASXOD_KLIENT"),

    VOZVRAT_KLIENT("RETURN_CLIENT", "ВОЗВРАТ_КЛИЕНТ", "VOZVRAT_KLIENT"),

    ACTUAL_BALANCE("ACTUAL_BALANCE", "АКТУАЛЬНЫЙ_БАЛАНС", "AKTUALNIY BALANS"),

    GODOWN("GO_DOWN", "GO_DOWN", "GO_DOWN"),

    ITEM_GROUP_CHANGE("ITEM_GROUP_CHANGE", "ITEM_GROUP_CHANGE", "ITEM_GROUP_CHANGE"),

    BRAK("BRAK", "BRAK", "BRAK");


    fun getLocaleValue(locale: Locale): String{
        when(locale.language){
            "en" -> return englishText
            "ru" -> return russianText
            "uz" -> return uzbekText
        }
        return russianText
    }
}

enum class Status{
    CREATING, CREATED, ACTIVE, DELETED, PASSIVE, UPDATED, TO_PAY
}

enum class TypeSource{
    DESKTOP, MOBILE
}

enum class BotState(private var botState: BotState?) {
    START_STATE(null),

    CURRENCY_STATE(START_STATE),

    DATE_STATE(CURRENCY_STATE),

    CALENDAR_STATE(DATE_STATE);

    fun getParentBotState() = botState
}

enum class IMG_TYPE{
    LOGO,
    FAVICON
}
