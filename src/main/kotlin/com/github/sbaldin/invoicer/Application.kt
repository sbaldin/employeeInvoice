package com.github.sbaldin.invoicer

import com.github.sbaldin.invoicer.domain.*
import com.github.sbaldin.invoicer.generator.InvoiceFactory
import com.github.sbaldin.invoicer.processor.SaveAsOfficeDocumentProcessor
import com.github.sbaldin.invoicer.processor.SaveAsPdfProcessor
import com.uchuhimo.konf.Config
import com.uchuhimo.konf.source.yaml
import com.uchuhimo.konf.toValue
import javafx.application.Application
import org.slf4j.Logger
import org.slf4j.LoggerFactory


val log: Logger = LoggerFactory.getLogger(Application::class.java)

private fun readAppConf(appConfPath: String, resourcePath: String = "application.yaml") =
    Config().from.yaml.file(appConfPath)
        .from.yaml.resource(resourcePath)
        .at("app").toValue<AppConf>()

private fun employeeDetails(appConfPath: String, resourcePath: String = "application.yaml") =
    Config().from.yaml.file(appConfPath)
        .from.yaml.resource(resourcePath)
        .at("employee").toValue<EmployeeDetailsModel>()

private fun readLocalBankingDetails(appConfPath: String, resourcePath: String = "application.yaml") =
    Config().from.yaml.file(appConfPath)
        .from.yaml.resource(resourcePath)
        .at("banking").at("local").toValue<LocalBankingModel>()

private fun readForeignBankingDetails(appConfPath: String, resourcePath: String = "application.yaml") =
    Config().from.yaml.file(appConfPath)
        .from.yaml.resource(resourcePath)
        .at("banking").at("foreign").toValue<ForeignBankingModel>()

fun main(args: Array<String>) {
    log.info("Args:" + args.joinToString())
    val appConfPath = if(args.isEmpty()) {
        log.warn("Args doesn't contain the path to Application.yaml, the default path will be used instead.")
        "./application-invoicer.yaml"
    } else {
        args[0]
    }
    log.info("Application config path:$appConfPath")

    val appConf = readAppConf(appConfPath)
    val invoiceFactory = InvoiceFactory(
        appConf,
        employeeDetails(appConfPath),
        readLocalBankingDetails(appConfPath),
        readForeignBankingDetails(appConfPath)
    )
    val outputPath = System.getProperty("user.home") + appConf.outputPath
    log.info(appConf.resultFileType.title)
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

    log.info("Invoices generation finished.")
}