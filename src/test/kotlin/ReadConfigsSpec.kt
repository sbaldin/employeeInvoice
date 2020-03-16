import com.github.sbaldin.invoicer.domain.AppRunTypeEnum
import com.github.sbaldin.invoicer.domain.ResultFileTypeEnum
import com.github.sbaldin.invoicer.readAppConf
import com.github.sbaldin.invoicer.readEmployeeDetails
import com.github.sbaldin.invoicer.readForeignBankingDetails
import com.github.sbaldin.invoicer.readLocalBankingDetails
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import java.io.File
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.util.TimeZone
import java.util.Date
import java.util.Locale
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
                assertEquals(AppRunTypeEnum.BOTH, defaultAppConfig.appRunType)
                assertEquals("/", defaultAppConfig.outputPath)
                assertEquals(ResultFileTypeEnum.OFFICE, defaultAppConfig.resultFileType)
            }

            it("should read office foreign invoice config") {
                val file = createTempYamlConfig(AppRunTypeEnum.FOREIGN_BANK_INVOICE, ResultFileTypeEnum.OFFICE)
                val defaultAppConfig = readAppConf(appConfPath = file.absolutePath)
                assertEquals(AppRunTypeEnum.FOREIGN_BANK_INVOICE, defaultAppConfig.appRunType)
                assertEquals("/", defaultAppConfig.outputPath)
                assertEquals(ResultFileTypeEnum.OFFICE, defaultAppConfig.resultFileType)
            }

            it("should read office local invoice config") {
                val file = createTempYamlConfig(AppRunTypeEnum.LOCAL_BANK_INVOICE, ResultFileTypeEnum.OFFICE)
                val defaultAppConfig = readAppConf(appConfPath = file.absolutePath)
                assertEquals(AppRunTypeEnum.LOCAL_BANK_INVOICE, defaultAppConfig.appRunType)
                assertEquals("/", defaultAppConfig.outputPath)
                assertEquals(ResultFileTypeEnum.OFFICE, defaultAppConfig.resultFileType)
            }
        }
        on("Reading Employee Conf") {
            val sdf = getSDF("dd.MM.yyyy", Locale("ru"))

            it("should read employee details") {
                val employeeDetails = readEmployeeDetails()
                assertEquals(employeeDetails.name, "Ivan Ivanov")
                assertEquals("31.12.2019", sdf.format(employeeDetails.contractDate))
                assertEquals("Platform Development", employeeDetails.serviceProvider)
                assertEquals(10, employeeDetails.vacationDaysInMonth)
                assertEquals(12, employeeDetails.vacationDaysInYear)
                assertEquals(3500, employeeDetails.monthRate)
                assertEquals(50, employeeDetails.additionalExpenses)
                assertEquals("build/resources/main/sign.png", employeeDetails.signPath)
                assertEquals(employeeDetails.formattedContractDate(), sdf.format(employeeDetails.contractDate))
            }
        }
        on("Banking Conf") {

            it("should read foreign bank config") {
                val foreignBankingDetails = readForeignBankingDetails()
                assertEquals("BANK", foreignBankingDetails.name)
                assertEquals("101010101", foreignBankingDetails.accountNumber)
                assertEquals("Google LLC", foreignBankingDetails.contractorName)
                assertEquals("10 10th Street NE Atlanta, GA 30309 United States", foreignBankingDetails.address)
            }

            it("should read local bank config. Russian locale.") {
                val localBankingDetails = readLocalBankingDetails()
                assertEquals("AO «BANK»", localBankingDetails.name)
                assertEquals("0000 0000 0000 0001 1111", localBankingDetails.accountNumber)
                assertEquals("Россия", localBankingDetails.country)
                assertEquals("Казань, пр Ленина, д.1, кв. 1", localBankingDetails.address)
                assertEquals("ИП Сергей Сергеевич Сергеев", localBankingDetails.beneficiaryName)
                assertEquals(
                    "Россия, Татарстан, г. Казань, пр. Ленина, д.1., кв. 1",
                    localBankingDetails.beneficiaryAddress
                )
            }

            it("should read local bank config. English locale.") {
                val localBankingDetails = readLocalBankingDetails(locale = Locale.ENGLISH)
                assertEquals("AO «BANK»", localBankingDetails.name)
                assertEquals("0000 0000 0000 0001 0001", localBankingDetails.accountNumber)
                assertEquals("Russia", localBankingDetails.country)
                assertEquals(
                    "27 Lenina str., Kazan, 107078, tel +7 495 755-58-58, SWIFT BANKRUM",
                    localBankingDetails.address
                )
                assertEquals("IP Sergey Sergeevich Sergeev", localBankingDetails.beneficiaryName)
                assertEquals("PR. LENINA, D. 1, KV. 1, KAZAN, RUSSIA, 650000", localBankingDetails.beneficiaryAddress)
            }
        }
    }
})

private fun getSDF(pattern: String, locale: Locale?) = SimpleDateFormat(pattern, locale).apply {
    timeZone = TimeZone.getTimeZone("GMT")
}

fun convertToDate(dateToConvert: LocalDate): Date? {
    return Date.from(
        dateToConvert.atStartOfDay()
            .atZone(ZoneId.systemDefault())
            .toInstant()
    )
}

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