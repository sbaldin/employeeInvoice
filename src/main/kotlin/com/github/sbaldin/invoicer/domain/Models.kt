package com.github.sbaldin.invoicer.domain

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

enum class RunTypeEnum {
    Employer,
    Banking,
    Both
}

/**
 *
 * BankingDetails:
name: AO «ALFA-BANK»
accountNumber: 00000 000 0000 0000 0000
country: Russia
bankAddress: 27 Kalanchevskaya str., Moscow, 107078, tel +7 495 755-58-58, SWIFT ALFARUMM
beneficiaryName: IP Ivanov Ivan Ivanovich
beneficiaryAdress: PR. LENINA, D. 1, KV. 1, KEMEROVO, RUSSIA, 650000
 */
data class BankingDetails
    (
    val name: String,
    val accountNumber: String,
    val country: String,
    val bankAddress: String,
    val beneficiaryName: String,
    val beneficiaryAdress: String
)

/**
 * Employee:
name: Ivan Ivanov
invoiceDate: Janurary 1, 1970
ContractDate: Janurary 1, 1970
serviceProvider: Platform Development
vacationDaysInMonth: 0
vacationDaysInYear: 0
monthRate: 0
additionalExpenses: 0
 */
data class EmployeeDetails
    (
    val name: String,
    val invoiceDate: Date,
    val contractDate: Date,
    val serviceProvider: String,
    val vacationDaysInMonth: Int,
    val vacationDaysInYear: Int,
    val monthRate: Int,
    val additionalExpenses: Int
) {

    fun formattedInvoiceDate() = SimpleDateFormat("MMMMM dd, yyyy").format(invoiceDate)!!
    fun invoiceNumber() = LocalDate.now().run { "$year-${month.value}-SB" }
    fun dateOfService() = SimpleDateFormat("MMMMM yyyy").format(Date())

}