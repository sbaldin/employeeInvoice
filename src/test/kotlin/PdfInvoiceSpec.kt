import com.github.sbaldin.invoicer.Application
import com.github.sbaldin.invoicer.domain.AppRunTypeEnum
import com.github.sbaldin.invoicer.readAppConf
import com.github.sbaldin.invoicer.readEmployeeDetails
import com.github.sbaldin.invoicer.readLocalizedLocalBankingDetails
import com.github.sbaldin.invoicer.readForeignBankingDetails
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.jsoup.Jsoup

class PdfInvoiceSpec : Spek({
    given("Pdf Invoicer") {
        on("User tries to generate local bank invoice") {
            it("should read pdf config") {
                val factory = Application(
                    readAppConf(),
                    readEmployeeDetails(),
                    readLocalizedLocalBankingDetails(),
                    readForeignBankingDetails()
                ).createInvoiceFactory()

                val invoice = factory.getPdfInvoiceList(AppRunTypeEnum.LOCAL_BANK_INVOICE).first()
                val jsoupDoc = Jsoup.parse(invoice.document, Charsets.UTF_8.displayName())
            }
        }
    }
})