package com.github.sbaldin.invoicer

import com.github.sbaldin.invoicer.domain.AppConf
import com.github.sbaldin.invoicer.domain.BankingDetails
import com.github.sbaldin.invoicer.domain.EmployeeDetails
import org.apache.poi.ss.usermodel.IndexedColors
import org.apache.poi.xssf.usermodel.XSSFFont
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File


class InvoiceGenerator(
    val appConf: AppConf,
    val employee: EmployeeDetails,
    val bankingDetails: BankingDetails
) {
    fun generate() {
        fillInEmployerTemplate()
//        when (appConf.runType) {
//            RunTypeEnum.Banking -> generateBanking()
//            RunTypeEnum.Employer -> fillInEmployerTemplate()
//            RunTypeEnum.Both -> generateBoth()
//        }

    }

    private fun generateBoth() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    private fun fillInEmployerTemplate() {
        val workbook = XSSFWorkbook(appConf.templatePath)
        workbook.getSheetAt(0).apply {
            var rowIndex = 0
            setRowValue(rowIndex++, 0, "${employee.name} RiskMatch Invoice")
            setRowValue(rowIndex++, 0, "Contract: dated as of ${employee.formattedInvoiceDate()}")
            setRowValue(rowIndex++, 0, "Invoice number: ${employee.invoiceNumber()}")
            setRowValue(rowIndex++, 0, "Date of service: ${employee.dateOfService()}")
            rowIndex+= 3
            setRowValue(rowIndex++, 1, employee.vacationDaysInMonth)
            setRowValue(rowIndex++, 1, employee.vacationDaysInYear)
            rowIndex++
            setRowValue(rowIndex++, 1, employee.monthRate)
            setRowValue(rowIndex++, 1, employee.additionalExpenses)
            rowIndex++
            setRowValue(rowIndex++, 1, employee.monthRate + employee.additionalExpenses)
            rowIndex+= 3
            setRowValue(rowIndex++, 1, bankingDetails.name)
            setRowValue(rowIndex++, 1, bankingDetails.accountNumber)
            setRowValue(rowIndex++, 1, bankingDetails.country)
            setRowValue(rowIndex++, 1, bankingDetails.bankAddress)
            setRowValue(rowIndex++, 1, bankingDetails.beneficiaryName)
            setRowValue(rowIndex, 1, bankingDetails.bankAddress)
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


    private fun generateBanking() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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