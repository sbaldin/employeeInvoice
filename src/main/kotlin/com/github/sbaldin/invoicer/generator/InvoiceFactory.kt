package com.github.sbaldin.invoicer.generator

import com.github.sbaldin.invoicer.domain.AppRunTypeEnum
import com.github.sbaldin.invoicer.domain.EmployeeDetailsModel
import com.github.sbaldin.invoicer.domain.ForeignBankingModel
import com.github.sbaldin.invoicer.domain.LocalizedLocalBankingModel
import com.github.sbaldin.invoicer.generator.invoice.PdfInvoice
import com.github.sbaldin.invoicer.generator.invoice.PoiInvoice
import com.github.sbaldin.invoicer.generator.invoice.poi.ForeignBankInvoice
import com.github.sbaldin.invoicer.generator.invoice.poi.LocalBankInvoice
import java.util.Locale
import com.github.sbaldin.invoicer.generator.invoice.pdf.ForeignBankInvoice as PDFForeignBankInvoice
import com.github.sbaldin.invoicer.generator.invoice.pdf.LocalBankInvoice as PDFLocalBankInvoice

class InvoiceFactory(
    private val employeeDetails: EmployeeDetailsModel,
    private val localizedLocalBankingModel: LocalizedLocalBankingModel,
    private val foreignBankingModel: ForeignBankingModel
) {

    fun getOfficeInvoiceList(runType: AppRunTypeEnum): List<PoiInvoice> = when (runType) {
        AppRunTypeEnum.FOREIGN_BANK_INVOICE -> listOf(
            ForeignBankInvoice(
                employeeDetails,
                localizedLocalBankingModel.getModel(Locale.ENGLISH)
            ).generate()
        )
        AppRunTypeEnum.LOCAL_BANK_INVOICE -> listOf(
            LocalBankInvoice(
                employeeDetails,
                localizedLocalBankingModel.getModel(Locale("ru")),
                foreignBankingModel
            ).generate()
        )
        AppRunTypeEnum.BOTH -> listOf(
            ForeignBankInvoice(
                employeeDetails,
                localizedLocalBankingModel.getModel(Locale.ENGLISH)
            ).generate(),
            LocalBankInvoice(
                employeeDetails,
                localizedLocalBankingModel.getModel(Locale("ru")),
                foreignBankingModel
            ).generate()
        )
    }

    fun getPdfInvoiceList(runType: AppRunTypeEnum): List<PdfInvoice> = when (runType) {
        AppRunTypeEnum.FOREIGN_BANK_INVOICE -> listOf(
            PDFForeignBankInvoice(
                employeeDetails,
                localizedLocalBankingModel.getModel(Locale.ENGLISH),
                foreignBankingModel,
                "/invoice_foreign.html"
            ).generate()
        )
        AppRunTypeEnum.LOCAL_BANK_INVOICE -> listOf(
            PDFLocalBankInvoice(
                employeeDetails,
                localizedLocalBankingModel.getModel(Locale("ru")),
                foreignBankingModel,
                "/invoice_local.html"
            ).generate()
        )
        AppRunTypeEnum.BOTH -> listOf(
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