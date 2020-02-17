package com.github.sbaldin.invoicer.domain

import java.lang.IllegalArgumentException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*


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
        modelByLocale[locale] ?: throw IllegalArgumentException("Missing Local Banking Model for giving locale = $locale!")

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

    init {

    }

    fun formattedContractDate() = SimpleDateFormat("dd.MM.yyyy").format(contractDate)
    fun formattedInvoiceDate(locale: Locale): String {
        return if (locale == Locale.ENGLISH) {
            DateTimeFormatter.ofPattern("dd MMMMM yyyy").withLocale(locale).format(getNowLocalDate())
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
    fun getDateOfService(locale: Locale) = SimpleDateFormat("LLLL yyyy", locale).format(getNow()).capitalize()

}