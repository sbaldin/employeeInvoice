package com.github.sbaldin.invoicer.generator.invoice.pdf

import com.github.sbaldin.invoicer.domain.EmployeeDetailsModel
import com.github.sbaldin.invoicer.domain.ForeignBankingModel
import com.github.sbaldin.invoicer.domain.LocalBankingModel
import com.github.sbaldin.invoicer.generator.invoice.InvoiceGenerator
import com.github.sbaldin.invoicer.generator.invoice.PdfInvoice
import freemarker.template.Configuration
import freemarker.template.Template
import freemarker.template.TemplateExceptionHandler
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.util.*


class LocalBankInvoice(
    employeeDetails: EmployeeDetailsModel,
    localBankingModel: LocalBankingModel,
    foreignBankingModel: ForeignBankingModel,
    templatePath: String
) : InvoiceGenerator,
    AbstractPdfBankInvoice(employeeDetails, localBankingModel, foreignBankingModel, templatePath) {

    override fun generate(): PdfInvoice {
        return PdfInvoice(
            name = "${employeeDetails.name} local invoice.pdf",
            document = generatePdfFile()
        )
    }

    override fun gePlaceholderModel(): HashMap<String, Any?> {
        val root = HashMap<String, Any?>()
        root["employee"] = employeeDetails
        root["formattedContractDate"] = employeeDetails.formattedContractDate()
        root["formattedPaymentDeadline"] = employeeDetails.formattedPaymentDeadline(Locale("ru"))
        root["formattedInvoiceDate"] = employeeDetails.formattedInvoiceDate(Locale("ru"))
        root["localBankingDetails"] = localBankingModel
        root["foreignBankingDetails"] = foreignBankingModel
        root["signPath"] = "./" + employeeDetails.signPath
        return root
    }
}

