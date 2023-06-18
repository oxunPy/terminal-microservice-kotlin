package com.example.terminaltelegrambot.calendar

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton
import java.time.LocalDate
import java.time.Month

class CalendarMarkupUtils {
    companion object{
        const val FROM_DATE = "from_date_"
        const val TO_DATE = "to_date_"
    }

    private fun convertToFromDateCalendarMarkup(calendarMarkup: InlineKeyboardMarkup?): InlineKeyboardMarkup {
        // change some button names as from date
        val nextYearIndex = getButtonListIndexByValue(
            calendarMarkup!!.keyboard[0],
            CalendarMarkup.NEXT_YEAR
        )
        val prevYearIndex = getButtonListIndexByValue(
            calendarMarkup.keyboard[0],
            CalendarMarkup.PREV_YEAR
        )
        val currentYearIndex = getButtonListIndexByValue(
            calendarMarkup.keyboard[0],
            CalendarMarkup.CURRENT_YEAR
        )
        val nextMonthIndex = getButtonListIndexByValue(
            calendarMarkup.keyboard[1],
            CalendarMarkup.NEXT_MONTH
        )
        val prevMonthIndex = getButtonListIndexByValue(
            calendarMarkup.keyboard[1],
            CalendarMarkup.PREV_MONTH
        )
        val currentMonthIndex = getButtonListIndexByValue(
            calendarMarkup.keyboard[1],
            CalendarMarkup.CURRENT_MONTH
        )
        calendarMarkup.keyboard[0][nextYearIndex].callbackData = FROM_DATE + CalendarMarkup.NEXT_YEAR
        calendarMarkup.keyboard[0][prevYearIndex].callbackData = FROM_DATE + CalendarMarkup.PREV_YEAR
        calendarMarkup.keyboard[0][currentYearIndex].callbackData = FROM_DATE + CalendarMarkup.CURRENT_YEAR
        calendarMarkup.keyboard[1][nextMonthIndex].callbackData = FROM_DATE + CalendarMarkup.NEXT_MONTH
        calendarMarkup.keyboard[1][prevMonthIndex].callbackData = FROM_DATE + CalendarMarkup.PREV_MONTH
        calendarMarkup.keyboard[1][currentMonthIndex].callbackData = FROM_DATE + CalendarMarkup.CURRENT_MONTH
        for (row in calendarMarkup.keyboard) {
            for (button in row) {
                if (Utils.parseInt(button.callbackData) != 0) button.callbackData = FROM_DATE + button.text
            }
        }

        // FROM DATE LABEL
        val fromDateLabelButton = InlineKeyboardButton()
        fromDateLabelButton.text = "с даты:"
        fromDateLabelButton.callbackData = "FROM DATE"
        val row: MutableList<InlineKeyboardButton> = ArrayList()
        row.add(fromDateLabelButton)
        calendarMarkup.keyboard.add(0, row)
        return calendarMarkup
    }

    fun convertToToDateCalendarMarkup(calendarMarkup: InlineKeyboardMarkup?): InlineKeyboardMarkup? {
        // change some button name as to date
        val nextYearIndex = getButtonListIndexByValue(
            calendarMarkup!!.keyboard[0],
            CalendarMarkup.NEXT_YEAR
        )
        val prevYearIndex = getButtonListIndexByValue(
            calendarMarkup.keyboard[0],
            CalendarMarkup.PREV_YEAR
        )
        val nextMonthIndex = getButtonListIndexByValue(
            calendarMarkup.keyboard[1],
            CalendarMarkup.NEXT_MONTH
        )
        val prevMonthIndex = getButtonListIndexByValue(
            calendarMarkup.keyboard[1],
            CalendarMarkup.PREV_MONTH
        )
        val currentYearIndex = getButtonListIndexByValue(
            calendarMarkup.keyboard[0],
            CalendarMarkup.CURRENT_YEAR
        )
        val currentMonthIndex = getButtonListIndexByValue(
            calendarMarkup.keyboard[1],
            CalendarMarkup.CURRENT_MONTH
        )
        calendarMarkup.keyboard[0][nextYearIndex].callbackData = TO_DATE + CalendarMarkup.NEXT_YEAR
        calendarMarkup.keyboard[0][prevYearIndex].callbackData = TO_DATE + CalendarMarkup.PREV_YEAR
        calendarMarkup.keyboard[0][currentYearIndex].callbackData = TO_DATE + CalendarMarkup.CURRENT_YEAR
        calendarMarkup.keyboard[1][nextMonthIndex].callbackData = TO_DATE + CalendarMarkup.NEXT_MONTH
        calendarMarkup.keyboard[1][prevMonthIndex].callbackData = TO_DATE + CalendarMarkup.PREV_MONTH
        calendarMarkup.keyboard[1][currentMonthIndex].callbackData = TO_DATE + CalendarMarkup.CURRENT_MONTH
        for (row in calendarMarkup.keyboard) {
            for (button in row) {
                if (Utils.parseInt(button.callbackData) !== 0) button.callbackData = TO_DATE + button.text
            }
        }

        // TO DATE LABEL
        val toDateLabelButton = InlineKeyboardButton()
        toDateLabelButton.text = "до даты:"
        toDateLabelButton.callbackData = "TO DATE"
        val row: MutableList<InlineKeyboardButton> = ArrayList()
        row.add(toDateLabelButton)
        calendarMarkup.keyboard.add(0, row)
        return calendarMarkup
    }

    fun getButtonListIndexByValue(list: List<InlineKeyboardButton>, buttonText: String): Int {
        for (i in list.indices) {
            if (list[i].callbackData == buttonText) {
                return i
            }
        }
        return -1
    }

    fun getFromDateToDateCalendar(): InlineKeyboardMarkup? {
        val doubleCalendarMarkup = InlineKeyboardMarkup()

        // CALENDARS
        val fromDateCalendar = convertToFromDateCalendarMarkup(
            CalendarMarkup()
                .getCalendarInstanceWithoutSubmit()
        )
        val toDateCalendar = convertToToDateCalendarMarkup(
            CalendarMarkup()
                .getCalendarInstance()
        )
        val doubleCalendarKeyboard: MutableList<List<InlineKeyboardButton>> = ArrayList()
        doubleCalendarKeyboard.addAll(fromDateCalendar!!.keyboard)
        doubleCalendarKeyboard.addAll(toDateCalendar!!.keyboard)
        doubleCalendarMarkup.keyboard = doubleCalendarKeyboard
        return doubleCalendarMarkup
    }


    fun getFromDateFromCalendar(replyMarkup: InlineKeyboardMarkup): LocalDate? {
        var year = LocalDate.now().year
        var month = LocalDate.now().monthValue
        var day = LocalDate.now().dayOfMonth
        for (row in replyMarkup.keyboard) {
            for (btn in row) {
                if (btn.callbackData == FROM_DATE + CalendarMarkup.CURRENT_YEAR) {
                    year = btn.text.toInt()
                } else if (btn.callbackData == FROM_DATE + CalendarMarkup.CURRENT_MONTH) {
                    month = Month.valueOf(btn.text).value
                } else if (btn.text.contains(CalendarMarkup.ACTIVE_FROM_DATE_SIGN)) {
                    day = btn.text.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0].toInt()
                }
            }
        }
        return LocalDate.of(year, month, day)
    }

    fun getToDateFromCalendar(replyMarkup: InlineKeyboardMarkup): LocalDate? {
        var year = LocalDate.now().year
        var month = LocalDate.now().monthValue
        var day = LocalDate.now().dayOfMonth
        for (row in replyMarkup.keyboard) {
            for (btn in row) {
                if (btn.callbackData == TO_DATE + CalendarMarkup.CURRENT_YEAR) {
                    year = btn.text.toInt()
                } else if (btn.callbackData == TO_DATE + CalendarMarkup.CURRENT_MONTH) {
                    month = Month.valueOf(btn.text).value
                } else if (btn.text.contains(CalendarMarkup.ACTIVE_TO_DATE_SIGN)) {
                    day = btn.text.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0].toInt()
                }
            }
        }
        return LocalDate.of(year, month, day)
    }

    fun getButtonByCallbackQuery(
        buttonGrid: List<List<InlineKeyboardButton>>,
        callBackQueryName: String
    ): InlineKeyboardButton? {
        for (row in buttonGrid) {
            for (button in row) {
                if (button.callbackData == callBackQueryName) {
                    return button
                }
            }
        }
        return null
    }

    fun cloneDateToDateCalendar(calendar: InlineKeyboardMarkup): InlineKeyboardMarkup? {
        val dateToDateMarkup = InlineKeyboardMarkup()
        val keyboard: MutableList<List<InlineKeyboardButton>> = ArrayList()
        var row: MutableList<InlineKeyboardButton> = ArrayList()
        for (keyboardRow in calendar.keyboard) {
            for (button in keyboardRow) {
                val clonedButton = InlineKeyboardButton()
                clonedButton.text = button.text
                clonedButton.callbackData = button.callbackData
                row.add(clonedButton)
            }
            keyboard.add(row)
            row = ArrayList()
        }
        dateToDateMarkup.keyboard = keyboard
        return dateToDateMarkup
    }

    fun updateDaysFromDate(calendar: InlineKeyboardMarkup, year: Int, monthVal: Int) {
        val daysInMonth = Utils.getNumberOfDaysInMonth(year, monthVal - 1)
        val lastFromDateDayRow = getLastRowFromDateFromDateToDateCalendar(calendar)
        var lastRow = calendar.keyboard[lastFromDateDayRow]
        var lastDay = lastRow[lastRow.size - 1].text.toInt()
        if (lastDay < daysInMonth) {
            if (lastDay == 28) {
                lastRow = ArrayList()
                calendar.keyboard.add(lastFromDateDayRow + 1, lastRow)
            }
            while (lastDay++ < daysInMonth) {
                val newButton = InlineKeyboardButton()
                newButton.text = lastDay.toString()
                newButton.callbackData = FROM_DATE + lastDay
                lastRow.add(newButton)
            }
        } else if (lastDay > daysInMonth) {
            while (lastDay-- > daysInMonth) {
                lastRow.removeAt(lastRow.size - 1)
            }
        }
    }

    fun updateDaysToDate(calendar: InlineKeyboardMarkup, year: Int, monthVal: Int) {
        val daysInMonth = Utils.getNumberOfDaysInMonth(year, monthVal - 1)
        val lastToDateDayRow = getLastRowToDateFromDateToDateCalendar(calendar)
        var lastRow = calendar.keyboard[lastToDateDayRow]
        var lastDay = lastRow[lastRow.size - 1].text.toInt()
        if (lastDay < daysInMonth) {
            if (lastDay == 28) {
                lastRow = ArrayList()
                calendar.keyboard.add(lastToDateDayRow + 1, lastRow)
            }
            while (lastDay++ < daysInMonth) {
                val newButton = InlineKeyboardButton()
                newButton.text = lastDay.toString()
                newButton.callbackData = TO_DATE + lastDay
                lastRow.add(newButton)
            }
        } else if (lastDay > daysInMonth) {
            while (lastDay-- > daysInMonth) {
                lastRow.removeAt(lastRow.size - 1)
            }
        }
    }

    private fun getLastRowFromDateFromDateToDateCalendar(calendar: InlineKeyboardMarkup): Int {
        val keyboard = calendar.keyboard
        var row: Int
        row = 0
        while (row < keyboard.size) {
            var callBackData = keyboard[row][0].callbackData // row's first button callBackData
            if (callBackData.startsWith(FROM_DATE) && hasMonthDayNumber(callBackData)) {
                while (row < keyboard.size && callBackData.startsWith(FROM_DATE) && hasMonthDayNumber(callBackData)) {
                    row++
                    callBackData = keyboard[row][0].callbackData
                }
                break
            }
            row++
        }
        return row - 1
    }

    private fun getLastRowToDateFromDateToDateCalendar(calendar: InlineKeyboardMarkup): Int {
        return calendar.keyboard.size - 2
    }


    fun hasMonthDayNumber(text: String): Boolean {
        for (i in 1..30) {
            if (text.contains(i.toString())) return true
        }
        return false
    }

    fun getActiveDay(markup: InlineKeyboardMarkup, activeSign: String?): InlineKeyboardButton? {
        for (row in markup.keyboard) {
            for (button in row) {
                if (button.text.contains(activeSign!!)) {
                    return button
                }
            }
        }
        return null
    }
}