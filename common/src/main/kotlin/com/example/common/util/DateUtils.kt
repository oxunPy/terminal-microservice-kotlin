package com.example.common.util

import java.text.DateFormat
import java.text.DateFormatSymbols
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.TimeUnit

class DateUtils {
    companion object{
        const val PATTERN_1 = "yyyy-MM-dd hh:mm:ss"
        const val PATTERN_2 = "dd-MM-yyyy"
        const val PATTERN_3 = "yyyy-MM-dd"
        const val  PATTERN4 = "dd-MMMM"
        const val  PATTERN5 = "HH:mm"
        const val  PATTERN6 = "yyyy/MM/dd"
        const val  PATTERN7 = "yyyyMMddHHmmss"
        const val  PATTERN8 = "dd-MM-yyyy HH:mm"
        const val  PATTERN9 = "yyyyMMddHHmmssSSS"
        const val  PATTERN10 = "ddMMyyyyHHmmss"
        const val  PATTERN11 = "dd-MM-yy HH:mm"

        fun dateToStringFormat1(date: Date?): String? {
            val dateFormat: DateFormat = SimpleDateFormat(PATTERN_1)
            return dateFormat.format(date)
        }

        fun dateToStringFormat2(date: Date?): String? {
            val dateFormat: DateFormat = SimpleDateFormat(PATTERN_2)
            return dateFormat.format(date)
        }

        fun stringToDateFormat1(dateStr: String?): Date? {
            val sdf = SimpleDateFormat(PATTERN_1)
            var convertedCurrentDate: Date? = null
            try {
                convertedCurrentDate = sdf.parse(dateStr)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return convertedCurrentDate
        }

        fun stringToLocalDateFormat(dateStr: String?): LocalDate? {
            val dateTimeFormatter = DateTimeFormatter.ofPattern(PATTERN_3)
            return LocalDate.parse(dateStr, dateTimeFormatter)
        }

        fun format(date: Date?, pattern: String?): String? {
            return if (date == null) null else SimpleDateFormat(pattern!!).format(date)
        }

        fun format(date: Date?, pattern: String?, locale: Locale?): String? {
            return if (date == null) null else SimpleDateFormat(pattern!!, locale!!).format(date)
        }

        fun parse(value: String?, pattern: String?): Date? {
            return if (value == null) {
                null
            } else {
                try {
                    SimpleDateFormat(pattern!!).parse(value)
                } catch (var3: ParseException) {
                    null
                }
            }
        }

        fun toLocale(date: Date?): LocalDate? {
            return date?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate()
        }

        fun toLocaleOrNow(date: Date?): LocalDate? {
            return if (date != null) date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate() else LocalDate.now()
        }

        fun fromLocale(date: LocalDate?): Date? {
            return if (date != null) {
                val instant = Instant.from(date.atStartOfDay(ZoneId.systemDefault()))
                Date.from(instant)
            } else {
                null
            }
        }

        fun fromLocaleOrNow(date: LocalDate?): Date? {
            return if (date != null) {
                val instant = Instant.from(date.atStartOfDay(ZoneId.systemDefault()))
                Date.from(instant)
            } else {
                Date()
            }
        }

        fun isValid(value: String?, pattern: String?): Boolean {
            return try {
                SimpleDateFormat(pattern).parse(value)
                true
            } catch (var3: ParseException) {
                false
            }
        }

        fun atStartOfDay(date: Date?): Date? {
            return if (date == null) {
                null
            } else {
                val calendar = Calendar.getInstance()
                calendar.time = date
                calendar[11] = 0
                calendar[12] = 0
                calendar[13] = 0
                calendar[14] = 0
                calendar.time
            }
        }

        fun atEndOfDay(date: Date?): Date? {
            return if (date == null) {
                null
            } else {
                val calendar = Calendar.getInstance()
                calendar.time = date
                calendar[11] = 23
                calendar[12] = 59
                calendar[13] = 59
                calendar[14] = 999
                calendar.time
            }
        }

        fun atStartOfMonth(date: Date?): Date? {
            return if (date == null) {
                null
            } else {
                val calendar = Calendar.getInstance()
                calendar.time = date
                calendar[11] = 0
                calendar[12] = 0
                calendar[13] = 0
                calendar[14] = 0
                calendar[5] = 1
                calendar.time
            }
        }

        fun atEndOfMonth(date: Date?): Date? {
            return if (date == null) {
                null
            } else {
                val calendar = Calendar.getInstance()
                calendar.time = date
                calendar[11] = 23
                calendar[12] = 59
                calendar[13] = 59
                calendar[14] = 999
                calendar[5] = calendar.getActualMaximum(5)
                calendar.time
            }
        }

        fun addMonths(date: Date?, months: Int): Date? {
            val calendar = Calendar.getInstance()
            calendar.time = date
            calendar.add(2, months)
            return calendar.time
        }

        fun addDays(date: Date?, days: Int): Date? {
            val calendar = Calendar.getInstance()
            calendar.time = date
            calendar.add(5, days)
            return calendar.time
        }

        fun addSeconds(date: Date?, seconds: Int): Date? {
            val calendar = Calendar.getInstance()
            calendar.time = date
            calendar.add(13, seconds)
            return calendar.time
        }

        fun dayOfWeek(date: Date?): Int {
            val calendar = Calendar.getInstance()
            calendar.time = date
            return calendar[7]
        }

        fun today(): Date? {
            val now = Date()
            return Date(now.date, now.month, now.date)
        }

        fun weekdays(): Array<String> {
            val locale = Locale.ENGLISH
            val dfs = DateFormatSymbols(locale)
            return dfs.weekdays
        }

        fun weekdaysAsMap(): Map<Int?, String?>? {
            val result: MutableMap<Int?, String?> = TreeMap<Int?, String?>()
            val weekdays = weekdays()
            for (i in weekdays.indices) {
                result[i] = weekdays[i]
            }
            return result
        }

        fun weekdaysAsMap(startDayOfWeek: Int): Map<Int?, String?>? {
            val result: MutableMap<Int?, String?> = LinkedHashMap<Int?, String?>()
            val weekdays = weekdays()
            var i: Int
            i = startDayOfWeek
            while (i < weekdays.size) {
                result[i] = weekdays[i]
                ++i
            }
            i = 0
            while (i < startDayOfWeek) {
                result[i] = weekdays[i]
                ++i
            }
            return result
        }

        fun weekdaysAsMap(startDayOfWeek: Int, locale: Locale?): Map<Int, Map<String, String>> {
            val result: MutableMap<Int, HashMap<String, String>> = LinkedHashMap<Int, HashMap<String, String>>()
            val weekdays = weekdays()
            var i: Int
            var weekday: HashMap<String, String> = HashMap<String, String>()
            i = startDayOfWeek
            while (i < weekdays.size) {
                weekday["name"] = weekdays[i]
                weekday["date"] = format(futureFirstDay(i), PATTERN4, locale)!!
                result[i] = weekday
                ++i
            }
            i = 0
            while (i < startDayOfWeek) {
                weekday["name"] = weekdays[i]
                weekday["date"] = format(futureFirstDay(i), PATTERN4, locale)!!
                result[i] = weekday
                ++i
            }
            return result
        }

        fun futureFirstDay(dayOfWeek: Int): Date? {
            val today = Date()
            return if (dayOfWeek <= 0) {
                today
            } else {
                val currentDayOfWeek = dayOfWeek(today)
                if (dayOfWeek >= currentDayOfWeek) addDays(today, dayOfWeek - currentDayOfWeek) else addDays(
                    today,
                    7 + dayOfWeek - currentDayOfWeek
                )
            }
        }

        fun monthsBetweenDates(d1: Date?, d2: Date?): Int {
            return if (d1 != null && d2 != null) {
                val calendar = Calendar.getInstance()
                calendar.time = d1
                val nMonth1 = 12 * calendar[1] + calendar[2]
                calendar.time = d2
                val nMonth2 = 12 * calendar[1] + calendar[2]
                Math.abs(nMonth2 - nMonth1)
            } else {
                -1
            }
        }

        fun getField(date: Date?, field: Int): Int {
            val calendar = Calendar.getInstance()
            calendar.time = date
            return calendar[field]
        }

        fun getHour(date: Date?): Int {
            return getField(date, 11)
        }

        fun getMinute(date: Date?): Int {
            return getField(date, 12)
        }

        fun getSecond(date: Date?): Int {
            return getField(date, 13)
        }

        fun getDatesBetween(from: Date?, to: Date?): List<Date?> {
            val datesInRange: MutableList<Date?> = ArrayList()
            return if (from != null && to != null) {
                val fromCal = Calendar.getInstance()
                fromCal.time = from
                while (fromCal.time.before(to)) {
                    datesInRange.add(fromCal.time)
                    fromCal.add(5, 1)
                }
                datesInRange
            } else {
                datesInRange
            }
        }

        fun daysBetween(a: Date?, b: Date?): Long {
            return TimeUnit.DAYS.convert(atStartOfDay(a)!!.time - atStartOfDay(b)!!.time, TimeUnit.MILLISECONDS)
        }
    }
}