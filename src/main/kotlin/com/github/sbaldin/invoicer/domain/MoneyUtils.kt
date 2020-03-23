package com.github.sbaldin.invoicer.domain

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

private var defaultDecimalFormatter: DecimalFormat? = null

fun getDecimalFormatter(): DecimalFormat {
    return if (defaultDecimalFormatter == null) {
        val l = DecimalFormatSymbols(Locale.ENGLISH)
        l.groupingSeparator = ','
        defaultDecimalFormatter = DecimalFormat("#,###", l)
        defaultDecimalFormatter
    } else {
        defaultDecimalFormatter
    }!!
}