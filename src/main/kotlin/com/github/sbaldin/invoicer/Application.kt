package com.github.sbaldin.invoicer

import com.github.sbaldin.invoicer.domain.*
import com.github.sbaldin.invoicer.generator.InvoiceFactory
import com.github.sbaldin.invoicer.processor.SaveAsOfficeDocumentProcessor
import com.github.sbaldin.invoicer.processor.SaveAsPdfProcessor
import com.uchuhimo.konf.Config
import com.uchuhimo.konf.source.yaml
import com.uchuhimo.konf.toValue
import org.apache.log4j.BasicConfigurator
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.text.SimpleDateFormat
import java.util.*


val log: Logger = LoggerFactory.getLogger(Application::class.java)

class Application {


    private fun readAppConf(appConfPath: String, resourcePath: String = "application-invoicer.yaml") =
        Config().from.yaml.resource(resourcePath)
            .from.yaml.file(appConfPath)
            .at("app").toValue<AppConf>()

    private fun employeeDetails(employeeConfPath: String, resourcePath: String = "employee.yaml") =
        Config().from.yaml.resource(resourcePath)
            .from.yaml.file(employeeConfPath)
            .at("employee").toValue<EmployeeDetailsModel>()

    private fun readLocalizedLocalBankingDetails(employeeConfPath: String, resourcePath: String = "employee.yaml") =
        LocalizedLocalBankingModel(
            mapOf(
                Locale.ENGLISH to readLocalBankingDetails(employeeConfPath, Locale("en"), resourcePath),
                Locale("ru") to readLocalBankingDetails(employeeConfPath, Locale("ru"), resourcePath)
            )
        )

    private fun readLocalBankingDetails(employeeConfPath: String, locale: Locale, resourcePath: String = "employee.yaml") =
        Config().from.yaml.resource(resourcePath)
            .from.yaml.file(employeeConfPath)
            .at("banking").at("local").at(locale.language.toLowerCase()).toValue<LocalBankingModel>()

    private fun readForeignBankingDetails(employeeConfPath: String, resourcePath: String = "employee.yaml") =
        Config().from.yaml.resource(resourcePath)
            .from.yaml.file(employeeConfPath)
            .at("banking").at("foreign").toValue<ForeignBankingModel>()

    fun run() {
        val appConfPath = System.getProperty("appConfig") ?: "./application-invoicer.yaml"
        val employeeConfPath = System.getProperty("employeeConfig") ?: "./employee.yaml"
        log.info("Application config path:$appConfPath")
        log.info("Employee config path:$appConfPath")

        System.getProperty("invoiceDate")?.let {
            setNow(SimpleDateFormat("dd.mm.yy").parse(it))
            log.info("Following date will be used instead of `now` ${getNow()}")
        }

        val appConf = readAppConf(appConfPath)

        val invoiceFactory = InvoiceFactory(
            employeeDetails(employeeConfPath = employeeConfPath),
            readLocalizedLocalBankingDetails(employeeConfPath = employeeConfPath),
            readForeignBankingDetails(employeeConfPath = employeeConfPath)
        )

        val outputPath = System.getProperty("user.home") + appConf.outputPath
        log.info("Invoice generation started. \n ${appConf.resultFileType.title}")

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

        log.info("Invoice generation completed.")
    }

}

fun main() {
    BasicConfigurator.configure()
    Application().run()
}