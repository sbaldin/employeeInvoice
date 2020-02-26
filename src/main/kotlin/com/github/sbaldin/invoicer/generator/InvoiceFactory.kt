package com.github.sbaldin.invoicer.generator

import com.github.sbaldin.invoicer.domain.*
import com.github.sbaldin.invoicer.domain.AppRunTypeEnum.*
import com.github.sbaldin.invoicer.generator.invoice.poi.ForeignBankInvoice
import com.github.sbaldin.invoicer.generator.invoice.PoiInvoice
import com.github.sbaldin.invoicer.generator.invoice.poi.LocalBankInvoice
import com.github.sbaldin.invoicer.generator.invoice.PdfInvoice
import java.util.*
import com.github.sbaldin.invoicer.generator.invoice.pdf.ForeignBankInvoice as PDFForeignBankInvoice
import com.github.sbaldin.invoicer.generator.invoice.pdf.LocalBankInvoice as PDFLocalBankInvoice


class InvoiceFactory(
    private val employeeDetails: EmployeeDetailsModel,
    private val localizedLocalBankingModel: LocalizedLocalBankingModel,
    private val foreignBankingModel: ForeignBankingModel
) {

    fun getOfficeInvoiceList(runType: AppRunTypeEnum): List<PoiInvoice> = when (runType) {
        FOREIGN_BANK_INVOICE -> listOf(
            ForeignBankInvoice(
                employeeDetails,
                localizedLocalBankingModel.getModel(Locale.ENGLISH)
            ).generate()
        )
        LOCAL_BANK_INVOICE -> listOf(
            LocalBankInvoice(
                employeeDetails,
                localizedLocalBankingModel.getModel(Locale("ru")),
                foreignBankingModel
            ).generate()
        )
        BOTH -> listOf(
            ForeignBankInvoice(
                employeeDetails,
                localizedLocalBankingModel.getModel(Locale.ENGLISH)
            ).generate(), LocalBankInvoice(
                employeeDetails,
                localizedLocalBankingModel.getModel(Locale("ru")),
                foreignBankingModel
            ).generate()
        )
    }

    fun getPdfInvoiceList(runType: AppRunTypeEnum): List<PdfInvoice> = when (runType) {
        FOREIGN_BANK_INVOICE -> listOf(
            PDFForeignBankInvoice(
                employeeDetails,
                localizedLocalBankingModel.getModel(Locale.ENGLISH),
                foreignBankingModel,
                "/invoice_foreign.html"
            ).generate()
        )
        LOCAL_BANK_INVOICE -> listOf(
            PDFLocalBankInvoice(
                employeeDetails,
                localizedLocalBankingModel.getModel(Locale("ru")),
                foreignBankingModel,
                "/invoice_local.html"
            ).generate()
        )
        BOTH -> listOf(
            PDFForeignBankInvoice(
                employeeDetails,
                localizedLocalBankingModel.getModel(Locale.ENGLISH),
                foreignBankingModel,
                "/invoice_foreign.html"
            ).generate(),
            PDFLocalBankInvoice(
                employeeDetails,
                localizedLocalBankingModel.getModel(Locale("ru")),
                foreignBankingModel,
                "/invoice_local.html"
            ).generate()
        )
    }
}