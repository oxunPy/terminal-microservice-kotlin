package com.example.common.constants

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDate

data class DateRangeAndCurrency(
    var currency: Currency? = null,

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    var fromDate: LocalDate? = null,

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    var toDate: LocalDate? = null
)