package com.github.sbaldin.invoicer.generator.invoice

import com.github.sbaldin.invoicer.domain.EmployeeDetails
import com.github.sbaldin.invoicer.domain.ForeignBankingDetails
import com.github.sbaldin.invoicer.domain.LocalBankingDetails
import org.apache.poi.POIXMLDocument
import org.apache.poi.xwpf.usermodel.*
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STLineSpacingRule
import java.io.File
import java.math.BigInteger
import java.util.*


class LocalBankInvoice(
    private val employee: EmployeeDetails,
    private val localBankingDetails: LocalBankingDetails,
    private val foreignBankingDetails: ForeignBankingDetails
) : InvoiceGenerator {

    override fun generate(): Invoice {
        val document = XWPFDocument()
        document.apply {
            singleLineParagraph("Инвойс № ${employee.invoiceNumber()} от ${employee.formattedInvoiceDate(Locale("ru"))}") {
                it.style = "Heading1"
                it.alignment = ParagraphAlignment.CENTER
                setSingleLineSpacing(it)

                val run = it.runs.first()
                run.fontFamily = "Roboto"
                run.isBold = true
            }
            createMonthRateTable()

            singleLineParagraph("Оплатить в срок до ${employee.formattedPaymentDeadline(Locale("ru"))}.")

            createForeignBankDetailsTable()
            createLocalBankDetails()

            singleLineParagraph("Подпись ") {
                val run = it.runs.first()
                run.fontFamily = "Roboto"
                val underLinePart = it.createRun()
                underLinePart.underline = UnderlinePatterns.SINGLE
                underLinePart.setText("             ")
                setDoublerLineSpacing(it)
            }
        }
        return Invoice(name = "${employee.name}_invoice.docx", document = document)
    }

    private fun XWPFDocument.createMonthRateTable() {
        val monthRateTable = createTable(2, 2).apply {
            //create first row
            //create first row
            
            val tableRowOne: XWPFTableRow = rows[0]
            setTextWithDefaultStyle(tableRowOne.getCell(0), "Описание услуг")
            setTextWithDefaultStyle(tableRowOne.getCell(1), "стоимость")

            val tableRowTwo: XWPFTableRow = rows[1]
            //TODO fix format here
            val jobDesc = "Разработка и поддержка программного обеспечения по договору " +
                    "об оказании услуг от ${employee.formattedContractDate()}" +
                    " за ${employee.formattedInvoiceDate(Locale("ru"))}."

            setTextWithDefaultStyle(tableRowTwo.getCell(0), jobDesc)
            setTextWithDefaultStyle(tableRowTwo.getCell(1), "$ ${employee.monthRate}")
        }
        createParagraph().body.insertTable(0, monthRateTable)
    }

    private fun XWPFDocument.createForeignBankDetailsTable() {
        val titleParagraph = createParagraph()
        val titleRun = titleParagraph.createRun()
        titleRun.fontFamily = "Roboto"
        titleRun.setText("Выставлен:")
        setDoublerLineSpacing(titleParagraph)
        val foreignBankDetailsTable = createTable(4, 2).apply {
            //create first row
            //create first row
            val bankName: XWPFTableRow = getRow(0)

            setTextWithDefaultStyle(bankName.getCell(0), "Наименование Банка")
            setTextWithDefaultStyle(bankName.getCell(1), foreignBankingDetails.name)

            val accountNumber: XWPFTableRow = getRow(1)
            setTextWithDefaultStyle(accountNumber.getCell(0), "Номер Счета")
            setTextWithDefaultStyle(accountNumber.getCell(1), foreignBankingDetails.accountNumber)

            val employeeName: XWPFTableRow = getRow(2)
            setTextWithDefaultStyle(employeeName.getCell(0), "Имя Отправителя")
            setTextWithDefaultStyle(employeeName.getCell(1), foreignBankingDetails.contractorName)

            val address: XWPFTableRow = getRow(3)
            setTextWithDefaultStyle(address.getCell(0), "Адрес")
            setTextWithDefaultStyle(address.getCell(1), foreignBankingDetails.address)
        }
        createParagraph().body.insertTable(0, foreignBankDetailsTable)
    }

    private fun XWPFDocument.createLocalBankDetails() {
        val titleParagraph = createParagraph()
        val titleRun = titleParagraph.createRun()
        titleRun.fontFamily = "Roboto"
        titleRun.setText("Получатель:")
        setDoublerLineSpacing(titleParagraph)
        val localBankDetailsTable = createTable(4, 2).apply {

            //create first row
            //create first row
            val bankName: XWPFTableRow = getRow(0)
            setTextWithDefaultStyle(bankName.getCell(0), "Наименование Банка")
            setTextWithDefaultStyle(bankName.getCell(1), localBankingDetails.name)

            val accountNumber: XWPFTableRow = getRow(1)
            setTextWithDefaultStyle(accountNumber.getCell(0), "Номер Счета")
            setTextWithDefaultStyle(accountNumber.getCell(1), localBankingDetails.accountNumber)

            val employeeName: XWPFTableRow = getRow(2)
            setTextWithDefaultStyle(employeeName.getCell(0), "Имя Получателя")
            setTextWithDefaultStyle(employeeName.getCell(1), localBankingDetails.beneficiaryName)

            val address: XWPFTableRow = getRow(3)
            setTextWithDefaultStyle(address.getCell(0), "Адрес")
            setTextWithDefaultStyle(address.getCell(1), localBankingDetails.beneficiaryAddress)
        }

        createParagraph().body.insertTable(0, localBankDetailsTable)
    }

    private fun XWPFDocument.singleLineParagraph(
        text: String,
        paragraphStyle: (XWPFParagraph) -> Unit = { setSingleLineSpacing(it) }
    ): XWPFParagraph? {
        val paragraph = createParagraph()
        val run = paragraph.createRun()
        run.fontFamily = "Roboto"
        run.setText(text)
        paragraphStyle(paragraph)
        return paragraph
    }

    private fun setTextWithDefaultStyle(cell: XWPFTableCell, text: String) {
        cell.verticalAlignment = XWPFTableCell.XWPFVertAlign.CENTER
        //Can't get first paragraph with elegant way, looks like ugly code is major feature of Apache POI
        val ctP: CTP = if (cell.ctTc.sizeOfPArray() == 0) cell.ctTc.addNewP() else cell.ctTc.getPArray(0)
        val paragraph = XWPFParagraph(ctP, cell)
        setSingleLineSpacing(paragraph)
        paragraph.alignment = ParagraphAlignment.CENTER
        paragraph.style = "Text Body"
        val run = paragraph.createRun()
        run.fontFamily = "Roboto"
        run.setText(text)
    }

    private fun setSingleLineSpacing(paragraph: XWPFParagraph) {
        var ppr = paragraph.ctp.pPr
        if (ppr == null) ppr = paragraph.ctp.addNewPPr()
        val spacing = if (ppr!!.isSetSpacing) ppr.spacing else ppr.addNewSpacing()
        spacing.after = BigInteger.valueOf(100)
        spacing.before = BigInteger.valueOf(100)
        spacing.lineRule = STLineSpacingRule.AUTO
        spacing.line = BigInteger.valueOf(240)
    }

    private fun setDoublerLineSpacing(paragraph: XWPFParagraph) {
        var ppr = paragraph.ctp.pPr
        if (ppr == null) ppr = paragraph.ctp.addNewPPr()
        val spacing = if (ppr!!.isSetSpacing) ppr.spacing else ppr.addNewSpacing()
        spacing.after = BigInteger.valueOf(200)
        spacing.before = BigInteger.valueOf(200)
        spacing.lineRule = STLineSpacingRule.AUTO
        spacing.line = BigInteger.valueOf(240)
    }
}