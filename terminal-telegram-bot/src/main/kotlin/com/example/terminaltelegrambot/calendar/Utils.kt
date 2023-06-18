package com.example.terminaltelegrambot.calendar

import java.time.Month
import java.util.*

class Utils {
    companion object{
        fun getPrevMonth(month: String): Month? {
            if (Month.JANUARY.toString() == month) {
                return Month.DECEMBER
            }
            val monthVal = getMonthValue(month)
            return Month.of(monthVal - 1)
        }

        fun getNextMonth(month: String): Month? {
            if (Month.DECEMBER.toString() == month) {
                return Month.JANUARY
            }
            val monthVal = getMonthValue(month)
            return Month.of(monthVal + 1)
        }

        fun getMonthValue(month: String): Int {
            var monthVal = 1
            for (m in Month.values()) {
                if (m.toString() == month) break
                monthVal++
            }
            return monthVal
        }

        fun getNumberOfDaysInMonth(year: Int, month: Int): Int {
            // Create a calendar object and set year and month
            val mycal: Calendar = GregorianCalendar(year, month, 1)
            return mycal.getActualMaximum(Calendar.DAY_OF_MONTH)
        }

        fun parseInt(number: String?): Int? {
            return try {
                Integer.valueOf(number)
            } catch (e: NumberFormatException) {
                0
            }
        }
    }
}