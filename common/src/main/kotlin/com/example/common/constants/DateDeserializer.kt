package com.example.common.constants

import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*

class DateDeserializer {
    private val format1: String = "yyyy-MM-dd HH:mm:ss"
    private val simpleDateFormat: SimpleDateFormat = SimpleDateFormat(format1)


    fun getDateFromStr(date: String): LocalDateTime {
        var resultDate: Date = Date()
        try{
            resultDate = simpleDateFormat.parse(date)
        } catch (ex: ParseException){
            ex.printStackTrace()
        }
        return LocalDateTime.of(resultDate.year, resultDate.month, resultDate.date, resultDate.hours, resultDate.minutes, resultDate.seconds)

    }


}