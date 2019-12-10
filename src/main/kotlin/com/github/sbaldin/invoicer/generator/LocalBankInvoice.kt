package com.github.sbaldin.invoicer.generator

import com.github.sbaldin.invoicer.domain.AppConf
import com.github.sbaldin.invoicer.domain.EmployeeDetails
import com.github.sbaldin.invoicer.domain.ForeignBankingDetails
import com.github.sbaldin.invoicer.domain.LocalBankingDetails
import org.apache.poi.xwpf.usermodel.*
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*
import java.io.File
import java.math.BigInteger
import java.util.*


class LocalBankInvoice(
   private val appConf: AppConf,
   private val employee: EmployeeDetails,
   private val localBankingDetails: LocalBankingDetails,
   private val foreignBankingDetails: ForeignBankingDetails
    ) : InvoiceGenerator {

    override fun generate() {
        val document = XWPFDocument()

        document.apply {
            val paragraph = createParagraph()
            val run = paragraph.createRun()
            run.setText("Инвойс № ${employee.invoiceNumber()} от ${employee.formattedInvoiceDate(Locale("ru"))}")
            run.fontFamily = "Roboto"
            run.isBold = true
            paragraph.style = "Heading1"
            paragraph.alignment = ParagraphAlignment.CENTER
            setSingleLineSpacing(paragraph)

            createMonthRateTable()

            createParagraph()
            val createParagraph = createParagraph()
            createParagraph.createRun().setText("Оплатить в срок до ${employee.formattedPaymentDeadline(Locale("ru"))}.")
            createParagraph()
            createParagraph().createRun().setText("Выставлен:")

            createForeignBankDetailsTable()
            createLocalBankDetails()

            createParagraph()
            createParagraph().createRun().setText("Подпись")
        }
        //create Paragraph

        File(appConf.outputPath + "/invoice_test.docx").outputStream().apply {
            document.write(this)
            close()
        }
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

    private fun XWPFDocument.createForeignBankDetailsTable(){
        val foreignBankDetailsTable = createTable().apply {
            //create first row
            //create first row
            val bankName: XWPFTableRow = getRow(0)
            bankName.getCell(0).text = "Наименование Банка"
            bankName.addNewTableCell().text = foreignBankingDetails.name

            val accountNumber: XWPFTableRow = createRow()
            accountNumber.getCell(0).text = "Номер Счета"
            accountNumber.getCell(1).text = foreignBankingDetails.accountNumber

            val employeeName: XWPFTableRow = createRow()
            employeeName.getCell(0).text = "Имя Отправителя"
            employeeName.getCell(1).text = foreignBankingDetails.contractorName

            val address: XWPFTableRow = createRow()
            address.getCell(0).text = "Адрес"
            address.getCell(1).text = foreignBankingDetails.address
        }
        createParagraph().body.insertTable(0, foreignBankDetailsTable)

    }
    
    private fun XWPFDocument.createLocalBankDetails() {
        createParagraph()
        createParagraph().createRun().setText("Получатель:")

        val localBankDetailsTable = createTable().apply {
            //create first row
            //create first row
            val bankName: XWPFTableRow = getRow(0)
            bankName.getCell(0).text = "Наименование Банка"
            bankName.addNewTableCell().text = localBankingDetails.name

            val accountNumber: XWPFTableRow = createRow()
            accountNumber.getCell(0).text = "Номер Счета"
            accountNumber.getCell(1).text = localBankingDetails.accountNumber

            val employeeName: XWPFTableRow = createRow()
            employeeName.getCell(0).text = "Имя Получателя"
            employeeName.getCell(1).text = localBankingDetails.beneficiaryName

            val address: XWPFTableRow = createRow()
            address.getCell(0).text = "Адрес"
            address.getCell(1).text = localBankingDetails.beneficiaryAddress
        }

        createParagraph().body.insertTable(0, localBankDetailsTable)
    }


    private fun XWPFDocument.addCustomHeadingStyle(
        styleId: String,
        headingLevel: Int
    ) {
        val ctStyle = CTStyle.Factory.newInstance()
        ctStyle.styleId = styleId
        val styleName = CTString.Factory.newInstance()
        styleName.setVal(styleId)
        ctStyle.name = styleName
        val indentNumber = CTDecimalNumber.Factory.newInstance()
        indentNumber.setVal(BigInteger.valueOf(headingLevel.toLong()))
        // lower number > style is more prominent in the formats bar
        ctStyle.uiPriority = indentNumber
        val onoffnull = CTOnOff.Factory.newInstance()
        ctStyle.unhideWhenUsed = onoffnull
        // style shows up in the formats bar
        ctStyle.qFormat = onoffnull
        // style defines a heading of the given level
        val ppr = CTPPr.Factory.newInstance()
        ppr.outlineLvl = indentNumber
        ctStyle.pPr = ppr
        val style = XWPFStyle(ctStyle)
        // is a null op if already defined
        val styles = createStyles()
        style.type = STStyleType.PARAGRAPH
        styles.addStyle(style)
    }



    private fun setTextWithDefaultStyle(cell: XWPFTableCell, text: String) {
        cell.verticalAlignment = XWPFTableCell.XWPFVertAlign.CENTER
        //Can't get first paragraph with elegant way, looks like ugly code is major feature of Apache POI
        val ctP: CTP = if (cell.ctTc.sizeOfPArray() == 0) cell.ctTc.addNewP() else cell.ctTc.getPArray(0)
        val par = XWPFParagraph(ctP, cell)
        setSingleLineSpacing(par)
        par.alignment = ParagraphAlignment.CENTER
        val run = par.createRun()
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
}