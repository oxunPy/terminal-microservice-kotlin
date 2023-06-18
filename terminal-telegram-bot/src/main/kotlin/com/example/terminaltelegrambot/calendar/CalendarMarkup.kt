package com.example.terminaltelegrambot.calendar

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton
import java.time.LocalDate

class CalendarMarkup {
    companion object{
        const val NEXT_YEAR = "next_year"
        const val PREV_YEAR = "prev_year"
        const val NEXT_MONTH = "next_month"
        const val PREV_MONTH = "prev_month"
        const val SUBMIT = "подтвердить"
        const val SUBMIT_DATE = "submit_date"
        const val CURRENT_YEAR = "current_year"
        const val CURRENT_MONTH = "current_month"
        const val ACTIVE_FROM_DATE_SIGN =  /*"\u221A"*/"\u2734"
        const val ACTIVE_TO_DATE_SIGN =  /*"\u1F5F8"*/"\u2733"
    }


    fun getCalendarInstance(): InlineKeyboardMarkup? {
        return generateCalendar(LocalDate.now(), true)
    }

    fun getCalendarInstanceWithoutSubmit(): InlineKeyboardMarkup? {
        return generateCalendar(LocalDate.now(), false)
    }

    private fun generateCalendar(date: LocalDate, withSubmit: Boolean): InlineKeyboardMarkup? {
        val markup = generateMonthKeyboard(date)
        val keyboard = markup.keyboard
        keyboard.addAll(generateDaysKeyboard(date, withSubmit)!!)
        return markup
    }

    fun generateDaysKeyboard(date: LocalDate, withSubmit: Boolean): List<List<InlineKeyboardButton>>? {
        val numberOfDays: Int = Utils.getNumberOfDaysInMonth(date.year, date.monthValue - 1)
        var row: MutableList<InlineKeyboardButton> = ArrayList()
        val keyboard: MutableList<List<InlineKeyboardButton>> = ArrayList()
        for (day in 1..numberOfDays) {
            if (day % 7 == 0) {
                val lastDayButton = InlineKeyboardButton()
                lastDayButton.text = day.toString()
                lastDayButton.callbackData = day.toString()
                lastDayButton.pay = false
                row.add(lastDayButton)
                keyboard.add(row)
                row = ArrayList()
            } else {
                val dayButton = InlineKeyboardButton()
                dayButton.text = day.toString()
                dayButton.callbackData = day.toString()
                dayButton.pay = false
                row.add(dayButton)
            }
        }
        if (!row.isEmpty()) keyboard.add(row)
        if (withSubmit) {
            val submit = InlineKeyboardButton()
            submit.text = SUBMIT
            submit.callbackData = SUBMIT_DATE
            row = ArrayList()
            row.add(submit)
            keyboard.add(row)
        }
        return keyboard
    }

    private fun generateYearKeyboard(year: Int): InlineKeyboardMarkup {
        val markup = InlineKeyboardMarkup()
        val row: MutableList<InlineKeyboardButton> = ArrayList()
        val prevYear = InlineKeyboardButton("<<")
        prevYear.callbackData = PREV_YEAR
        row.add(prevYear) // previous year
        val currentYear = InlineKeyboardButton(year.toString())
        currentYear.callbackData = CURRENT_YEAR
        row.add(currentYear) // previous year
        val nextYear = InlineKeyboardButton(">>")
        nextYear.callbackData = NEXT_YEAR
        row.add(nextYear) // previous year
        val keyboard: MutableList<List<InlineKeyboardButton>> = ArrayList()
        keyboard.add(row)
        markup.keyboard = keyboard
        return markup
    }

    fun generateMonthKeyboard(date: LocalDate): InlineKeyboardMarkup {
        val markup = generateYearKeyboard(date.year)
        val row: MutableList<InlineKeyboardButton> = ArrayList()
        val prevMonth = InlineKeyboardButton("<<")
        prevMonth.callbackData = PREV_MONTH
        row.add(prevMonth) // previous year
        val currentMonth = InlineKeyboardButton(date.month.toString())
        currentMonth.callbackData = CURRENT_MONTH
        row.add(currentMonth) // previous year
        val nextMonth = InlineKeyboardButton(">>")
        nextMonth.callbackData = NEXT_MONTH
        row.add(nextMonth) // previous year
        val keyboard = markup.keyboard
        keyboard.add(row)
        return markup
    }

}