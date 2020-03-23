import com.github.sbaldin.invoicer.Application
import com.github.sbaldin.invoicer.domain.AppRunTypeEnum
import com.github.sbaldin.invoicer.domain.ResultFileTypeEnum
import com.github.sbaldin.invoicer.domain.getNowLocalDate
import com.github.sbaldin.invoicer.processor.SaveAsOfficeDocumentProcessor
import com.github.sbaldin.invoicer.readAppConf
import com.github.sbaldin.invoicer.readEmployeeDetails
import com.github.sbaldin.invoicer.readForeignBankingDetails
import com.github.sbaldin.invoicer.readLocalizedLocalBankingDetails
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import java.io.File
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.test.assertEquals

class XlsxInvoiceSpec : Spek({
    given("Xlsx Foreign Bank Invoice") {
        val testAppConfig = createTempYamlConfig(AppRunTypeEnum.FOREIGN_BANK_INVOICE, ResultFileTypeEnum.OFFICE)

        val localizedLocalBankingDetails = readLocalizedLocalBankingDetails()
        val localBankingModel = localizedLocalBankingDetails.getModel(Locale.ENGLISH)
        val employeeDetails = readEmployeeDetails()
        val foreignBankingDetails = readForeignBankingDetails()

        val factory = Application(
            readAppConf(appConfPath = testAppConfig.absolutePath),
            employeeDetails,
            localizedLocalBankingDetails,
            foreignBankingDetails
        ).createInvoiceFactory()

        val dateTimeFormatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy").withLocale(Locale.ENGLISH)
        val decimalFormat = createDecimalFormat()

        val invoice = factory.getOfficeInvoiceList(AppRunTypeEnum.FOREIGN_BANK_INVOICE).first()
        val outputPath = File(".").absolutePath
        val processor = SaveAsOfficeDocumentProcessor(outputPath)
        processor.process(listOf(invoice))
        val invoiceFilePath = outputPath + "/" + invoice.name
        val invoiceFile = File(invoiceFilePath)

        afterGroup {
            testAppConfig.deleteOnExit()
            invoiceFile.deleteOnExit()
        }

        on("User tries to generate foreign bank invoice") {
            it("invoice should be valid workbook") {
                assertEquals(true, invoiceFile.inputStream().readBytes().isNotEmpty())
            }
            val workbook = XSSFWorkbook(invoiceFile.inputStream())
            it("invoice should contain correct title") {
                withFirstSheet(workbook) {
                    assertEquals("${employeeDetails.name} RiskMatch Invoice", getCellValue(0, 0))
                }
            }

            it("invoice should contain correct invoice info") {
                withFirstSheet(workbook) {
                    val invoiceDate = dateTimeFormatter.format(getNowLocalDate())

                    assertEquals("Invoice date: $invoiceDate", getCellValue(1, 0))
                    assertEquals(
                        "Contract: dated as of ${employeeDetails.formattedContractDate(pattern = "MMMM dd, yyyy")}",
                        getCellValue(2, 0)
                    )
                    assertEquals("Invoice number: ${employeeDetails.getInvoiceNumber()}", getCellValue(3, 0))
                    assertEquals(
                        "Date of service: ${employeeDetails.getDateOfService(Locale.ENGLISH)}",
                        getCellValue(4, 0)
                    )
                }
            }

            it("invoice should contain correct vacation days taken in month and year") {
                withFirstSheet(workbook) {
                    assertEquals("${employeeDetails.vacationDaysInMonth}", getCellValue(7, 1))
                    assertEquals("${employeeDetails.vacationDaysInYear}", getCellValue(8, 1))
                }
            }

            it("invoice should contain correct month rate") {
                withFirstSheet(workbook) {
                    assertEquals(decimalFormat.format(employeeDetails.monthRate), getCellValue(10, 1))
                    assertEquals("0", getCellValue(11, 1)) // temporary do not add additional expenses
                    assertEquals(decimalFormat.format(employeeDetails.monthRate), getCellValue(13, 1)) // temporary do not add additional expenses
                }
            }

            it("invoice should contain correct local banking info") {
                withFirstSheet(workbook) {
                    assertEquals(localBankingModel.name, getCellValue(17, 1))
                    assertEquals(localBankingModel.accountNumber, getCellValue(18, 1))
                    assertEquals(localBankingModel.country, getCellValue(19, 1))
                    assertEquals(localBankingModel.address, getCellValue(20, 1))

                    assertEquals(localBankingModel.beneficiaryName, getCellValue(27, 1))
                    assertEquals(localBankingModel.beneficiaryAddress, getCellValue(28, 1))
                }
            }
        }
    }
})

private fun createDecimalFormat(): DecimalFormat {
    val l = DecimalFormatSymbols(Locale.ENGLISH)
    l.groupingSeparator = ','
    return DecimalFormat("#,###", l)
}

private fun withFirstSheet(workbook: XSSFWorkbook, body: XSSFSheet.() -> Unit) {
    workbook.apply {
        getSheetAt(0).apply(body)
    }
}

private fun XSSFSheet.getCellValue(rowIndex: Int, cellIndex: Int) =
    getRow(rowIndex).getCell(cellIndex).stringCellValue