import com.github.sbaldin.invoicer.Application
import com.github.sbaldin.invoicer.domain.getNow
import com.github.sbaldin.invoicer.domain.setNow
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.test.assertEquals

class InvoiceDateSpec : Spek({
    given("Invoice Date Changeability") {
        on("System properties have invoiceDate") {
            it("Now Date should be replaced by system property invoiceDate") {
                System.setProperty("invoiceDate", "15.5.2030")
                val app = Application()
                app.createInvoiceFactory()
                assertEquals(SimpleDateFormat("dd.MM.yyyy").parse(System.getProperty("invoiceDate")), getNow())

                System.setProperty("invoiceDate", "10.12.2030")
                app.createInvoiceFactory()
                assertEquals(SimpleDateFormat("dd.MM.yyyy").parse(System.getProperty("invoiceDate")), getNow())

                System.setProperty("invoiceDate", "1.1.2020")
                app.createInvoiceFactory()
                assertEquals(SimpleDateFormat("dd.MM.yyyy").parse(System.getProperty("invoiceDate")), getNow())
            }
        }
        on("System properties have no invoiceDate") {
            it("Now Date should be provided by instantiation of Date object") {
                setNow(Date())
                val properties = System.getProperties()
                properties.remove("invoiceDate")
                System.setProperties(properties)

                val app = Application()
                app.createInvoiceFactory()

                val sdf = SimpleDateFormat("dd.MM.yyyy")
                assertEquals(sdf.format(Date()), sdf.format(getNow()))
            }
        }
    }
})