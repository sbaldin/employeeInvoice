package com.github.sbaldin.invoicer

import java.util.*

enum class RunTypeEnum{
        Employeer,
        Banking,
        Both
}

data class AppConf(
        val runType : RunTypeEnum,
        val sendEmail : String
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
data class Employee
        (
        val name: String,
        val invoiceDate: Date,
        val ContractDate: Date,
        val serviceProvider: String,
        val vacationDaysInMonth: Int,
        val vacationDaysInYear: Int,
        val monthRate: Int,
        val additionalExpenses: Int
)

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