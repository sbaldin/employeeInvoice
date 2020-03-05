import com.github.sbaldin.invoicer.Application
import com.github.sbaldin.invoicer.domain.AppConf
import com.github.sbaldin.invoicer.domain.getNow
import com.github.sbaldin.invoicer.domain.setNow
import com.uchuhimo.konf.Config
import com.uchuhimo.konf.source.yaml
import com.uchuhimo.konf.toValue
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.spyk
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import java.text.SimpleDateFormat
import java.util.*
import kotlin.test.assertEquals
/*

class PdfLocalInvoiceSpec : Spek({

    given("Local Bank Invoice") {

        on("User requests invoice") {
            it("invoice should contains correct data") {
                val app = Application(

                )
                app.createInvoiceFactory()


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


private fun readAppConf(resourcePath: String = "application-invoicer.yaml") =
    Config().from.yaml.resource(resourcePath)
        .at("app").toValue<AppConf>()*/
