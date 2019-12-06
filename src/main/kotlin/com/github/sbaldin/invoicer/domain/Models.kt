package com.github.sbaldin.invoicer.domain

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*


enum class RunTypeEnum {
    Employer,
    Banking,
    Both
}

data class LocalBankingDetails(
    val name: String,
    val accountNumber: String,
    val country: String,
    val address: String,
    val beneficiaryName: String,
    val beneficiaryAddress: String
)

data class ForeignBankingDetails(
    val name: String,
    val accountNumber: String,
    val contractorName: String,
    val address: String
)

data class EmployeeDetails(
    val name: String,
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

    fun invoiceNumber() = LocalDate.now().run { "$year-${month.value}-SB" }
    fun dateOfService() = SimpleDateFormat("MMMMM yyyy").format(Date())

}