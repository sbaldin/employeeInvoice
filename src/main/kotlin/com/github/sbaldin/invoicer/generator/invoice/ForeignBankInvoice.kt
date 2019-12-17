package com.github.sbaldin.invoicer.generator.invoice

import com.github.sbaldin.invoicer.domain.EmployeeDetails
import com.github.sbaldin.invoicer.domain.LocalBankingDetails
import org.apache.poi.POIXMLDocument
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook


class ForeignBankInvoice(
    private val employee: EmployeeDetails,
    private val localBankingDetails: LocalBankingDetails,
    private val templatePath: String
) : InvoiceGenerator {

    override fun generate(): Invoice = Invoice(name = "${employee.name}_invoice.xlsx", document = XSSFWorkbook(templatePath).apply {
        getSheetAt(0).apply {
            var rowIndex = 0
            setRowValue(rowIndex++, 0, "${employee.name} RiskMatch Invoice")
            setRowValue(rowIndex++, 0, "${employee.name} RiskMatch Invoice")
            setRowValue(rowIndex++, 0, "Contract: dated as of ${employee.formattedContractDate()}")
            setRowValue(rowIndex++, 0, "Invoice number: ${employee.invoiceNumber()}")
            setRowValue(rowIndex++, 0, "Date of service: ${employee.dateOfService()}")
            rowIndex += 2 // skipping rows
            setRowValue(rowIndex++, 1, employee.vacationDaysInMonth)
            setRowValue(rowIndex++, 1, employee.vacationDaysInYear)
            rowIndex++    // skipping rows
            setRowValue(rowIndex++, 1, employee.monthRate)
            setRowValue(rowIndex++, 1, employee.additionalExpenses)
            rowIndex++    // skipping rows
            setRowValue(rowIndex++, 1, employee.monthRate + employee.additionalExpenses)
            rowIndex += 3 // skipping rows
            setRowValue(rowIndex++, 1, localBankingDetails.name)
            setRowValue(rowIndex++, 1, localBankingDetails.accountNumber)
            setRowValue(rowIndex++, 1, localBankingDetails.country)
            setRowValue(rowIndex++, 1, localBankingDetails.address)
            setRowValue(rowIndex++, 1, localBankingDetails.beneficiaryName)
            setRowValue(rowIndex, 1, localBankingDetails.address)
        }
    })

    private fun XSSFSheet.setRowValue(rowIndex: Int, cellIndex: Int, value: Any) {
        getRow(rowIndex).getCell(cellIndex).setCellValue(value.toString())
    }
}

