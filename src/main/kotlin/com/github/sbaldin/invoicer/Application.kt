package com.github.sbaldin.invoicer

import com.github.sbaldin.invoicer.domain.AppConf
import com.github.sbaldin.invoicer.domain.EmployeeDetailsModel
import com.github.sbaldin.invoicer.domain.ForeignBankingModel
import com.github.sbaldin.invoicer.domain.LocalBankingModel
import com.github.sbaldin.invoicer.domain.LocalizedLocalBankingModel
import com.github.sbaldin.invoicer.domain.ResultFileTypeEnum
import com.github.sbaldin.invoicer.domain.getNow
import com.github.sbaldin.invoicer.domain.setNow
import com.github.sbaldin.invoicer.generator.InvoiceFactory
import com.github.sbaldin.invoicer.processor.SaveAsOfficeDocumentProcessor
import com.github.sbaldin.invoicer.processor.SaveAsPdfProcessor
import com.github.sbaldin.invoicer.time.measureTimeMillis
import com.uchuhimo.konf.Config
import com.uchuhimo.konf.source.yaml
import com.uchuhimo.konf.toValue
import org.apache.log4j.BasicConfigurator
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.text.SimpleDateFormat
import java.util.Locale

val log: Logger = LoggerFactory.getLogger(Application::class.java)

fun readAppConf(appConfPath: String = "", resourcePath: String = "application-invoicer.yaml") =
    Config().from.yaml.resource(resourcePath)
        .from.yaml.file(appConfPath, optional = true)
        .at("app").toValue<AppConf>()

fun readEmployeeDetails(employeeConfPath: String = "", resourcePath: String = "employee.yaml") =
    Config().from.yaml.resource(resourcePath)
        .from.yaml.file(employeeConfPath, optional = true)
        .at("employee").toValue<EmployeeDetailsModel>()

fun readLocalizedLocalBankingDetails(employeeConfPath: String = "", resourcePath: String = "employee.yaml") =
    LocalizedLocalBankingModel(
        mapOf(
            Locale.ENGLISH to readLocalBankingDetails(employeeConfPath, Locale.ENGLISH, resourcePath),
            Locale("ru") to readLocalBankingDetails(employeeConfPath, Locale("ru"), resourcePath)
        )
    )

fun readLocalBankingDetails(employeeConfPath: String = "", locale: Locale  = Locale("ru"), resourcePath: String = "employee.yaml") =
    Config().from.yaml.resource(resourcePath)
        .from.yaml.file(employeeConfPath, optional = true)
        .at("banking").at("local").at(locale.language.toLowerCase()).toValue<LocalBankingModel>()

fun readForeignBankingDetails(employeeConfPath: String = "", resourcePath: String = "employee.yaml") =
    Config().from.yaml.resource(resourcePath)
        .from.yaml.file(employeeConfPath, optional = true)
        .at("banking").at("foreign").toValue<ForeignBankingModel>()

class Application(
    private val appConf: AppConf = readAppConf(),
    private val employeeDetails: EmployeeDetailsModel = readEmployeeDetails(),
    private val localizedLocalBankingModel: LocalizedLocalBankingModel = readLocalizedLocalBankingDetails(),
    private val foreignBankingModel: ForeignBankingModel = readForeignBankingDetails()
) {

    fun createInvoiceFactory(): InvoiceFactory {
        System.getProperty("invoiceDate")?.let {
            setNow(SimpleDateFormat("dd.MM.yyyy").parse(it))
            log.info("Following date will be used instead of `now` ${getNow()}")
        }
        return InvoiceFactory(
            employeeDetails,
            localizedLocalBankingModel,
            foreignBankingModel
        )
    }

    fun run() {
        val invoiceFactory = createInvoiceFactory()
        val outputPath = System.getProperty("user.home") + appConf.outputPath

        measureTimeMillis(
            { log.info("Invoice generation started. Result File Title: \n ${appConf.resultFileType.title}.") },
            { spentTime -> log.info("Invoice generation finished in ${spentTime / 1000} second.") }
        ) {
            when (appConf.resultFileType) {
                ResultFileTypeEnum.OFFICE -> {
                    val invoices = invoiceFactory.getOfficeInvoiceList(appConf.appRunType)
                    SaveAsOfficeDocumentProcessor(outputPath).process(invoices)
                }
                ResultFileTypeEnum.PDF -> {
                    val invoices = invoiceFactory.getPdfInvoiceList(appConf.appRunType)
                    SaveAsPdfProcessor(outputPath).process(invoices)
                }
            }
        }
    }
}

fun main() {
    BasicConfigurator.configure()

    val appConfPath: String = System.getProperty("appConfig") ?: "./application-invoicer.yaml"
    log.info("Application config path:$appConfPath")

    val employeeConfPath: String = System.getProperty("employeeConfig") ?: "./employee.yaml"
    log.info("Employee config path:$employeeConfPath")

    Application(
        readAppConf(appConfPath = appConfPath),
        readEmployeeDetails(employeeConfPath = employeeConfPath),
        readLocalizedLocalBankingDetails(employeeConfPath = employeeConfPath),
        readForeignBankingDetails(employeeConfPath = employeeConfPath)
    ).run()
}
