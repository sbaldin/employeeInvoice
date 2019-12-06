package com.github.sbaldin.invoicer

import com.github.sbaldin.invoicer.domain.*
import com.github.sbaldin.invoicer.generator.LocalBankInvoice
import org.apache.poi.ss.usermodel.IndexedColors
import org.apache.poi.xssf.usermodel.XSSFFont
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.apache.poi.xwpf.usermodel.XWPFDocument
import org.apache.poi.xwpf.usermodel.XWPFTableRow
import java.io.File
import java.util.*


class InvoiceGenerator(
    val appConf: AppConf,
    val employee: EmployeeDetails,
    val localBankingDetails: LocalBankingDetails,
    val foreignBankingDetails: ForeignBankingDetails
) {

    fun generate() {
        LocalBankInvoice(appConf, employee, localBankingDetails, foreignBankingDetails).generate()
    }

    private fun generateBoth() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    private fun fillInForiengBankReport() {
        val workbook = XSSFWorkbook(appConf.foreignTemplatePath)
        workbook.getSheetAt(0).apply {
            var rowIndex = 0
            setRowValue(rowIndex++, 0, "${employee.name} RiskMatch Invoice")
            setRowValue(rowIndex++, 0, "${employee.name} RiskMatch Invoice")
            setRowValue(rowIndex++, 0, "Contract: dated as of ${employee.formattedContractDate()}")
            setRowValue(rowIndex++, 0, "Invoice number: ${employee.invoiceNumber()}")
            setRowValue(rowIndex++, 0, "Date of service: ${employee.dateOfService()}")
            rowIndex += 2
            setRowValue(rowIndex++, 1, employee.vacationDaysInMonth)
            setRowValue(rowIndex++, 1, employee.vacationDaysInYear)
            rowIndex++
            setRowValue(rowIndex++, 1, employee.monthRate)
            setRowValue(rowIndex++, 1, employee.additionalExpenses)
            rowIndex++
            setRowValue(rowIndex++, 1, employee.monthRate + employee.additionalExpenses)
            rowIndex += 3
            setRowValue(rowIndex++, 1, localBankingDetails.name)
            setRowValue(rowIndex++, 1, localBankingDetails.accountNumber)
            setRowValue(rowIndex++, 1, localBankingDetails.country)
            setRowValue(rowIndex++, 1, localBankingDetails.address)
            setRowValue(rowIndex++, 1, localBankingDetails.beneficiaryName)
            setRowValue(rowIndex, 1, localBankingDetails.address)
        }
        workbook.write(File(appConf.outputPath + "/invoice_test.xlsx").outputStream())
    }

    private fun XSSFSheet.setRowValue(rowIndex: Int, cellIndex: Int, value: Any) {
        getRow(rowIndex).getCell(cellIndex).setCellValue(value.toString())
    }


    private fun XSSFSheet.createSimpleRow(rowNumber: Int, value: String) {
        createRow(rowNumber).let { row ->
            row.createCell(1).apply {
                setCellValue(value)
            }
        }
    }
}


fun XSSFWorkbook.createBoldFontStyle(): XSSFFont {
    val font: XSSFFont = createFont()
    font.fontHeightInPoints = 10.toShort()
    font.fontName = "Arial"
    font.color = IndexedColors.WHITE.getIndex()
    font.bold = true
    font.italic = false
    return font
}