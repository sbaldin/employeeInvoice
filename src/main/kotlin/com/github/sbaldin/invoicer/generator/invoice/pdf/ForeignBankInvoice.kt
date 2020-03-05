package com.github.sbaldin.invoicer.generator.invoice.pdf

import com.github.sbaldin.invoicer.domain.EmployeeDetailsModel
import com.github.sbaldin.invoicer.domain.ForeignBankingModel
import com.github.sbaldin.invoicer.domain.LocalBankingModel
import com.github.sbaldin.invoicer.generator.invoice.InvoiceGenerator
import com.github.sbaldin.invoicer.generator.invoice.PdfInvoice
import java.util.Locale
import kotlin.collections.HashMap

class ForeignBankInvoice(
    employeeDetails: EmployeeDetailsModel,
    localBankingModel: LocalBankingModel,
    foreignBankingModel: ForeignBankingModel,
    templatePath: String
) : InvoiceGenerator,
    AbstractPdfBankInvoice(employeeDetails, localBankingModel, foreignBankingModel, templatePath) {

    override fun generate(): PdfInvoice {
        val invoiceFile = generatePdfFile()
        return PdfInvoice(
            name = "${employeeDetails.name} foreign invoice.pdf",
            document = invoiceFile
        )
    }

    override fun gePlaceholderModel(): HashMap<String, Any?> {
        val root = HashMap<String, Any?>()
        root["employee"] = employeeDetails
        root["dateOfService"] = employeeDetails.getDateOfService(Locale.ENGLISH)
        root["localBankingDetails"] = localBankingModel
        root["foreignBankingDetails"] = foreignBankingModel
        root["formattedContractDate"] = employeeDetails.formattedContractDate()
        root["formattedPaymentDeadline"] = employeeDetails.formattedPaymentDeadline(Locale.US)
        root["formattedInvoiceDate"] = employeeDetails.formattedInvoiceDate(Locale.US, "MMMM dd, yyyy")
        root["localBankingDetails"] = localBankingModel
        root["foreignBankingDetails"] = foreignBankingModel
        root["signPath"] = "./" + employeeDetails.signPath
        if (employeeDetails.signPath.isNotBlank()) {
            root["signPath"] = "./" + employeeDetails.signPath
        }
        return root
    }
}
