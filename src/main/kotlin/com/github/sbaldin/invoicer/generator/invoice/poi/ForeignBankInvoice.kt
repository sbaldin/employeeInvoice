package com.github.sbaldin.invoicer.generator.invoice.poi

import com.github.sbaldin.invoicer.domain.EmployeeDetailsModel
import com.github.sbaldin.invoicer.domain.LocalBankingModel
import com.github.sbaldin.invoicer.generator.invoice.InvoiceGenerator
import com.github.sbaldin.invoicer.generator.invoice.PoiInvoice
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.util.*


class ForeignBankInvoice(
    private val employeeDetails: EmployeeDetailsModel,
    private val localBankingModel: LocalBankingModel
) : InvoiceGenerator {

    override fun generate(): PoiInvoice =
        PoiInvoice(
            name = "${employeeDetails.name}_invoice.xlsx",
            document = javaClass.getResourceAsStream("/invoice_foreign.xlsx").let {
                XSSFWorkbook(it).apply {
                    getSheetAt(0).apply {
                        var rowIndex = 0
                        setRowValue(rowIndex++, 0, "${employeeDetails.name} RiskMatch Invoice")
                        setRowValue(rowIndex++, 0, "${employeeDetails.name} RiskMatch Invoice")
                        setRowValue(rowIndex++, 0, "Contract: dated as of ${employeeDetails.formattedContractDate()}")
                        setRowValue(rowIndex++, 0, "Invoice number: ${employeeDetails.getInvoiceNumber()}")
                        setRowValue(rowIndex++, 0, "Date of service: ${employeeDetails.getDateOfService(Locale.ENGLISH)}")
                        rowIndex += 2 // skipping rows
                        setRowValue(rowIndex++, 1, employeeDetails.vacationDaysInMonth)
                        setRowValue(rowIndex++, 1, employeeDetails.vacationDaysInYear)
                        rowIndex++    // skipping rows
                        setRowValue(rowIndex++, 1, employeeDetails.monthRate)
                        setRowValue(rowIndex++, 1, employeeDetails.additionalExpenses)
                        rowIndex++    // skipping rows
                        setRowValue(rowIndex++, 1, employeeDetails.monthRate + employeeDetails.additionalExpenses)
                        rowIndex += 3 // skipping rows
                        setRowValue(rowIndex++, 1, localBankingModel.name)
                        setRowValue(rowIndex++, 1, localBankingModel.accountNumber)
                        setRowValue(rowIndex++, 1, localBankingModel.country)
                        setRowValue(rowIndex++, 1, localBankingModel.address)
                        setRowValue(rowIndex++, 1, localBankingModel.beneficiaryName)
                        setRowValue(rowIndex, 1, localBankingModel.address)
                    }
                }
            })

    private fun XSSFSheet.setRowValue(rowIndex: Int, cellIndex: Int, value: Any) {
        getRow(rowIndex).getCell(cellIndex).setCellValue(value.toString())
    }
}

