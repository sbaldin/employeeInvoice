package com.github.sbaldin.invoicer.domain

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
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
    val invoiceDate: Date,
    val contractDate: Date,
    val serviceProvider: String,
    val vacationDaysInMonth: Int,
    val vacationDaysInYear: Int,
    val monthRate: Int,
    val additionalExpenses: Int
) {

    fun formattedContractDate() = SimpleDateFormat("dd.MM.yyyy").format(invoiceDate)
    fun formattedInvoiceDate(locale: Locale)= SimpleDateFormat("dd MMMMM yyyy", locale).format(invoiceDate)
    fun formattedPaymentDeadline(locale: Locale) = LocalDate.now().plusDays(15).format(DateTimeFormatter.ofPattern("dd MMMM yyyy", locale))

    fun getInvoiceNumber() = LocalDate.now().run { "$year-${month.value}-SB" }
    fun getDateOfService(locale: Locale) = SimpleDateFormat("MMMMM yyyy", locale).format(Date())

}