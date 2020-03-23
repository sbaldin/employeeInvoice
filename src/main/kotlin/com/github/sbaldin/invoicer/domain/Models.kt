package com.github.sbaldin.invoicer.domain

import java.lang.IllegalArgumentException
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.format.FormatStyle
import java.time.format.TextStyle
import java.time.temporal.ChronoField
import java.util.Date
import java.util.Locale

enum class AppRunTypeEnum {
    FOREIGN_BANK_INVOICE,
    LOCAL_BANK_INVOICE,
    BOTH
}

enum class ResultFileTypeEnum(val title: String) {
    PDF("Application will produce full-filled pdf files with signatures."),
    OFFICE("Application will produce editable docx and xlsx files without signature.")
}

data class LocalizedLocalBankingModel(
    private val modelByLocale: Map<Locale, LocalBankingModel>
) {

    fun <T> withLocalization(locale: Locale, body: (LocalBankingModel) -> T) {
        val model = getModel(locale)
        body(model)
    }

    fun getModel(locale: Locale) =
        modelByLocale[locale]
            ?: throw IllegalArgumentException("Missing Local Banking Model for giving locale = $locale!")
}

data class LocalBankingModel(
    val name: String,
    val accountNumber: String,
    val country: String,
    val address: String,
    val beneficiaryName: String,
    val beneficiaryAddress: String
)

data class ForeignBankingModel(
    val name: String,
    val accountNumber: String,
    val contractorName: String,
    val address: String
)

data class EmployeeDetailsModel(
    val name: String,
    val signPath: String,
    val contractDate: Date,
    val serviceProvider: String,
    val vacationDaysInMonth: Int,
    val vacationDaysInYear: Int,
    val monthRate: Int,
    val additionalExpenses: Int
) {

    fun formattedContractDate(locale: Locale = Locale.ENGLISH, pattern: String = "dd.MM.yyyy") =
        SimpleDateFormat(pattern, locale).format(contractDate)

    fun formattedInvoiceDate(locale: Locale, pattern: String = "dd MMMM yyyy"): String {
        return if (locale == Locale.US) {
            DateTimeFormatter.ofPattern(pattern).withLocale(locale).format(getNowLocalDate())
        } else {
            DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).withLocale(Locale("ru")).format(getNowLocalDate())
        }
    }

    fun formattedPaymentDeadline(locale: Locale): String {
        val deadLine = getNowLocalDate().plusDays(15)
        return if (locale == Locale.ENGLISH) {
            deadLine.format(DateTimeFormatter.ofPattern("dd MMMM yyyy", locale))
        } else {
            DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).withLocale(Locale("ru")).format(deadLine)
        }
    }

    fun getInvoiceNumber() = getNowLocalDate().run { "$year-${month.value}-SB" }

    fun getDateOfService(locale: Locale): String = DateTimeFormatterBuilder().apply {
        // Can't find common way to format english and russian date
        if (locale != Locale.US) {
            appendText(ChronoField.MONTH_OF_YEAR, TextStyle.FULL_STANDALONE)
            appendLiteral(" ")
            appendText(ChronoField.YEAR)
        } else {
            appendPattern("MMMM yyyy")
        }
    }.toFormatter(locale).format(getNowLocalDate())

    fun abc() {

    }
}