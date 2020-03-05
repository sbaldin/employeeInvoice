package com.github.sbaldin.invoicer.domain

import java.time.LocalDate
import java.time.ZoneId
import java.util.Date

private var fakeNow: Date? = null

private var nowAsLocalDate: LocalDate? = null

fun getNow(): Date = fakeNow ?: Date()

fun getNowLocalDate(): LocalDate {
    return if (nowAsLocalDate != null) {
        nowAsLocalDate
    } else {
        nowAsLocalDate = getNow().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
        nowAsLocalDate
    }!!
}

fun setNow(date: Date?) {
    fakeNow = date
}
