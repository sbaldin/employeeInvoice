import com.github.sbaldin.invoicer.domain.AppRunTypeEnum
import com.github.sbaldin.invoicer.domain.ResultFileTypeEnum
import com.github.sbaldin.invoicer.readEmployeeDetails
import com.github.sbaldin.invoicer.readAppConf
import com.github.sbaldin.invoicer.readForeignBankingDetails
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import kotlin.test.assertEquals

class ReadConfigsSpec : Spek({

    given("App Conf") {
        on("Read App Conf") {
            it("should read pdf config") {
                val defaultAppConfig = readAppConf()
                assertEquals(AppRunTypeEnum.BOTH, defaultAppConfig.appRunType)
                assertEquals("/", defaultAppConfig.outputPath)
                assertEquals(ResultFileTypeEnum.PDF, defaultAppConfig.resultFileType)
            }

            it("should read office config") {
                val file = createTempYamlConfig(AppRunTypeEnum.BOTH, ResultFileTypeEnum.OFFICE)
                val defaultAppConfig = readAppConf(appConfPath = file.absolutePath)
                assertEquals(defaultAppConfig.appRunType, AppRunTypeEnum.BOTH)
                assertEquals(defaultAppConfig.outputPath, "/")
                assertEquals(defaultAppConfig.resultFileType, ResultFileTypeEnum.OFFICE)
            }

            it("should read office foreign invoice config") {
                val file = createTempYamlConfig(AppRunTypeEnum.FOREIGN_BANK_INVOICE, ResultFileTypeEnum.OFFICE)
                val defaultAppConfig = readAppConf(appConfPath = file.absolutePath)
                assertEquals(defaultAppConfig.appRunType, AppRunTypeEnum.FOREIGN_BANK_INVOICE)
                assertEquals(defaultAppConfig.outputPath, "/")
                assertEquals(defaultAppConfig.resultFileType, ResultFileTypeEnum.OFFICE)
            }

            it("should read office local invoice config") {
                val file = createTempYamlConfig(AppRunTypeEnum.LOCAL_BANK_INVOICE, ResultFileTypeEnum.OFFICE)
                val defaultAppConfig = readAppConf(appConfPath = file.absolutePath)
                assertEquals(defaultAppConfig.appRunType, AppRunTypeEnum.LOCAL_BANK_INVOICE)
                assertEquals(defaultAppConfig.outputPath, "/")
                assertEquals(defaultAppConfig.resultFileType, ResultFileTypeEnum.OFFICE)
            }
        }
        on("Reading Employee Conf") {
            it("should read employee details") {
                val employeeDetails = readEmployeeDetails()
                assertEquals(employeeDetails.name, "Ivan Ivanov")
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
                sdf.timeZone = TimeZone.getTimeZone("GMT");
                assertEquals(employeeDetails.contractDate, sdf.parse("2019-12-31"))
                assertEquals(employeeDetails.serviceProvider, "Platform Development")
                assertEquals(employeeDetails.vacationDaysInMonth, 10)
                assertEquals(employeeDetails.vacationDaysInYear, 12)
                assertEquals(employeeDetails.monthRate, 3500)
                assertEquals(employeeDetails.additionalExpenses, 50)
                assertEquals(employeeDetails.signPath, "build/resources/main/sign.png")
            }
            it("should read foreign bank config") {
                val foreignBankingDetails = readForeignBankingDetails()
                assertEquals(foreignBankingDetails.name, "BANK")
                assertEquals(foreignBankingDetails.accountNumber, "101010101")
                assertEquals(foreignBankingDetails.contractorName, "Google LLC")
                assertEquals(foreignBankingDetails.address, "10 10th Street NE Atlanta, GA 30309 United States")
            }
        }
    }
})

private fun createTempYamlConfig(runType: AppRunTypeEnum, fileType: ResultFileTypeEnum): File {
    return File.createTempFile("office", ".yaml").apply {
        writer().apply {
            write("app:\n")
            write("    appRunType: $runType #FOREIGN_BANK_INVOICE #LOCAL_BANK_INVOICE\n")
            write("    resultFileType: $fileType #OFFICE #PDF\n")
            write("    outputPath: / #relative path from user home directory\n")
        }.close()
    }
}