package com.github.sbaldin.invoicer

import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.ss.usermodel.IndexedColors
import org.apache.poi.ss.util.CellRangeAddress
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



    private fun fillInEmployerTemplate(){
        val workbook = XSSFWorkbook("invoice_template.xlsx")
        workbook.getSheetAt(0).apply {
            var rowIndex = generateSequence(1) {  it + 1 }.takeWhile { it < 100 }
            setRowValue(rowIndex.single(), 0, "${employee.name} RiskMatch Invoice")
            setRowValue(rowIndex.single(), 0, "Contract: dated as of ${employee.contractDate}")
            setRowValue(rowIndex.single(), 0, "Invoice number: ${employee.invoiceNumber}")
            setRowValue(rowIndex.single(), 0, "Date of service: ${employee.dateOfService}")
            rowIndex = rowIndex.drop(2)
            setRowValue(rowIndex.single(), 1, employee.vacationDaysInMonth)
            setRowValue(rowIndex.single(), 1, employee.vacationDaysInYear)
            rowIndex = rowIndex.drop(1)
            setRowValue(rowIndex.single(), 1, employee.monthRate)
            setRowValue(rowIndex.single(), 1, employee.additionalExpenses)
            rowIndex = rowIndex.drop(1)
            setRowValue(rowIndex.single(), 1, employee.monthRate + employee.additionalExpenses)
            rowIndex = rowIndex.drop(3)
            setRowValue(rowIndex.single(), 1, bankingDetails.name)
            setRowValue(rowIndex.single(), 1, bankingDetails.accountNumber)
            setRowValue(rowIndex.single(), 1, bankingDetails.country)
            setRowValue(rowIndex.single(), 1, bankingDetails.bankAddress)
            setRowValue(rowIndex.single(), 1, bankingDetails.beneficiaryName)
            setRowValue(rowIndex.single(), 1, bankingDetails.bankAddress)
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