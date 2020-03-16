import com.github.sbaldin.invoicer.Application
import com.github.sbaldin.invoicer.domain.AppRunTypeEnum
import com.github.sbaldin.invoicer.readAppConf
import com.github.sbaldin.invoicer.readEmployeeDetails
import com.github.sbaldin.invoicer.readForeignBankingDetails
import com.github.sbaldin.invoicer.readLocalizedLocalBankingDetails
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.jsoup.Jsoup
import java.text.NumberFormat
import java.util.Locale
import kotlin.test.assertEquals

class PdfInvoiceSpec : Spek({
    given("Pdf Invoicer") {
        val localizedLocalBankingDetails = readLocalizedLocalBankingDetails()
        val employeeDetails = readEmployeeDetails()
        val foreignBankingDetails = readForeignBankingDetails()
        val factory = Application(
            readAppConf(),
            employeeDetails,
            localizedLocalBankingDetails,
            foreignBankingDetails
        ).createInvoiceFactory()

        val invoice = factory.getPdfInvoiceList(AppRunTypeEnum.LOCAL_BANK_INVOICE).first()
        val jsoupDoc = Jsoup.parse(invoice.document, Charsets.UTF_8.displayName())
        afterGroup {
            invoice.document.deleteOnExit()
        }
        on("User tries to generate local bank invoice") {

            it("html version of invoice should contain correct title") {
                val invoiceTitle = jsoupDoc.getElementById("invoice-title")
                val expectedText =
                    "Инвойс № ${employeeDetails.getInvoiceNumber()} от ${employeeDetails.formattedInvoiceDate(Locale("ru"))}"
                assertEquals(expectedText, invoiceTitle.html().trim())
            }
            it("html version of invoice should contain correct payment info") {
                val mainTextBlock1 = jsoupDoc.getElementById("main-text-1")
                val mainTextBlock2 = jsoupDoc.getElementById("main-text-2")
                val mainTextBlock3 = jsoupDoc.getElementById("main-text-3")

                val formattedContractDate = employeeDetails.formattedContractDate()
                val dateOfService = employeeDetails.getDateOfService(Locale("ru"))
                val expectedText1 = """
                    Разработка и поддержка программного обеспечения по договору об оказании услуг от $formattedContractDate за $dateOfService года
                                           """.trimIndent()

                val formatter = NumberFormat.getInstance(Locale("en_US"))

                val expectedText2 = "$ ${formatter.format(employeeDetails.monthRate)}".trimIndent()
                val expectedText3 = "$ ${employeeDetails.additionalExpenses}".trimIndent()

                assertEquals(expectedText1, mainTextBlock1.html().trim())
                assertEquals(expectedText2, mainTextBlock2.html().trim())
                assertEquals(expectedText3, mainTextBlock3.html().trim())
            }

            it("html version of invoice should contain correct deadline info") {
                val deadLineText = jsoupDoc.getElementById("deadline-text")
                assertEquals(
                    "Оплатить в срок до ${employeeDetails.formattedPaymentDeadline(Locale("ru"))}",
                    deadLineText.html().trim()
                )
            }

            it("html version of invoice should contain correct foreign bank details") {
                val foreignBankingDetailsName = jsoupDoc.getElementById("foreign-banking-details-name")
                val foreignBankingDetailsAccountNumber =
                    jsoupDoc.getElementById("foreign-banking-details-account-number")
                val foreignBankingDetailsContractorName =
                    jsoupDoc.getElementById("foreign-banking-details-contractor-name")
                val foreignBankingDetailsAddress = jsoupDoc.getElementById("foreign-banking-details-address")

                assertEquals(
                    foreignBankingDetails.name,
                    foreignBankingDetailsName.html().trim()
                )
                assertEquals(
                    foreignBankingDetails.accountNumber,
                    foreignBankingDetailsAccountNumber.html().trim()
                )
                assertEquals(
                    foreignBankingDetails.contractorName,
                    foreignBankingDetailsContractorName.html().trim()
                )
                assertEquals(
                    foreignBankingDetails.address,
                    foreignBankingDetailsAddress.html().trim()
                )
            }

            it("html file of invoice should be full fulled by correct data") {
                val localBankingDetailsName = jsoupDoc.getElementById("local-banking-details-name")
                val localBankingDetailsAccountNumber = jsoupDoc.getElementById("local-banking-details-account-number")
                val localBankingDetailsContractorName =
                    jsoupDoc.getElementById("local-banking-details-beneficiary-name")
                val localBankingDetailsAddress = jsoupDoc.getElementById("local-banking-details-address")

                val model = localizedLocalBankingDetails.getModel(Locale("ru"))
                assertEquals(
                    model.name,
                    localBankingDetailsName.html().trim()
                )
                assertEquals(
                    model.accountNumber,
                    localBankingDetailsAccountNumber.html().trim()
                )
                assertEquals(
                    model.beneficiaryName,
                    localBankingDetailsContractorName.html().trim()
                )
                assertEquals(
                    model.address,
                    localBankingDetailsAddress.html().trim()
                )

            }

            it("html file of invoice should contains correct sign-path") {
                val signPath = jsoupDoc.getElementById("sign-path")
                assertEquals("build/resources/main/sign.png", signPath.attr("src"))
            }

        }
    }
})