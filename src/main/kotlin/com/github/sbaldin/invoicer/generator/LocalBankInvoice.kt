package com.github.sbaldin.invoicer.generator

import com.github.sbaldin.invoicer.domain.AppConf
import com.github.sbaldin.invoicer.domain.EmployeeDetails
import com.github.sbaldin.invoicer.domain.ForeignBankingDetails
import com.github.sbaldin.invoicer.domain.LocalBankingDetails
import org.apache.poi.xwpf.usermodel.XWPFDocument
import org.apache.poi.xwpf.usermodel.XWPFStyle
import org.apache.poi.xwpf.usermodel.XWPFTableRow
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
            paragraph.style = "Heading1"

            createMonthRateTable()

            createParagraph()
            createParagraph().createRun().setText("Оплатить в срок до ${employee.formattedPaymentDeadline(Locale("ru"))}.")
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
            tableRowOne.getCell(0).text = "Описание услуг"
            tableRowOne.getCell(1).text = "стоимость"

            val tableRowTwo: XWPFTableRow = rows[1]
            //TODO fix format here
            tableRowTwo.getCell(0).text = "Разработка и поддержка программного обеспечения по договору " +
                                                            "об оказании услуг от ${employee.formattedContractDate()}" +
                                                            " за ${employee.formattedInvoiceDate(
                                                                Locale("ru")
                                                            )}."
            tableRowTwo.getCell(1).text = "$ ${employee.monthRate}"
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
}