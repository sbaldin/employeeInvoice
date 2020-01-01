package com.github.sbaldin.invoicer.generator

import com.github.sbaldin.invoicer.domain.*
import com.github.sbaldin.invoicer.domain.RunTypeEnum.*
import com.github.sbaldin.invoicer.generator.invoice.poi.ForeignBankInvoice
import com.github.sbaldin.invoicer.generator.invoice.poi.Invoice
import com.github.sbaldin.invoicer.generator.invoice.poi.LocalBankInvoice


class InvoiceFactory(
    val appConf: AppConf,
    val employee: EmployeeDetails,
    private val localBankingDetails: LocalBankingDetails,
    private val foreignBankingDetails: ForeignBankingDetails
) {

    fun getOfficeInvoiceList(runType: RunTypeEnum): List<Invoice> = when (runType) {
        ForeignInvoice -> listOf(
            ForeignBankInvoice(
                employee,
                localBankingDetails,
                appConf.foreignTemplatePath
            ).generate()
        )
        LocalInvoice -> listOf(
            LocalBankInvoice(
                employee,
                localBankingDetails,
                foreignBankingDetails
            ).generate()
        )
        Both -> listOf(
            ForeignBankInvoice(
                employee,
                localBankingDetails,
                appConf.foreignTemplatePath
            ).generate(), LocalBankInvoice(
                employee,
                localBankingDetails,
                foreignBankingDetails
            ).generate()
        )
    }

    fun getPdfInvoiceList(runType: RunTypeEnum): List<Invoice> = when (runType) {
        ForeignInvoice -> listOf(
            ForeignBankInvoice(
                employee,
                localBankingDetails,
                appConf.foreignTemplatePath
            ).generate()
        )
        LocalInvoice -> listOf(
            LocalBankInvoice(
                employee,
                localBankingDetails,
                foreignBankingDetails
            ).generate()
        )
        Both -> listOf(
            ForeignBankInvoice(
                employee,
                localBankingDetails,
                appConf.foreignTemplatePath
            ).generate(), LocalBankInvoice(
                employee,
                localBankingDetails,
                foreignBankingDetails
            ).generate()
        )
    }
}