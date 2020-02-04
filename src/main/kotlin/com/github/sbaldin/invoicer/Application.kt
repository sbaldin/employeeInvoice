package com.github.sbaldin.invoicer

import com.github.sbaldin.invoicer.domain.*
import com.github.sbaldin.invoicer.generator.InvoiceFactory
import com.github.sbaldin.invoicer.processor.SaveAsOfficeDocumentProcessor
import com.github.sbaldin.invoicer.processor.SaveAsPdfProcessor
import com.uchuhimo.konf.Config
import com.uchuhimo.konf.source.yaml
import com.uchuhimo.konf.toValue
import javafx.application.Application
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random


val log: Logger = LoggerFactory.getLogger(Application::class.java)

private fun readAppConf(appConfPath: String, resourcePath: String = "application.yaml") =
    Config().from.yaml.resource(resourcePath)
        .from.yaml.file(appConfPath)
        .at("app").toValue<AppConf>()

private fun employeeDetails(appConfPath: String, resourcePath: String = "application.yaml") =
    Config().from.yaml.resource(resourcePath)
        .from.yaml.file(appConfPath)
        .at("employee").toValue<EmployeeDetailsModel>()

private fun readLocalBankingDetails(appConfPath: String, resourcePath: String = "application.yaml") =
    Config().from.yaml.resource(resourcePath)
        .from.yaml.file(appConfPath)
        .at("banking").at("local").toValue<LocalBankingModel>()

private fun readForeignBankingDetails(appConfPath: String, resourcePath: String = "application.yaml") =
    Config().from.yaml.resource(resourcePath)
        .from.yaml.file(appConfPath)
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
    if(args.size > 1 ){
       setNow(SimpleDateFormat("dd.mm.yy").parse(args[1]))
        log.info("Following date will be used instead of `now` ${getNow()}")
    }

    val appConf = readAppConf(appConfPath)
    val invoiceFactory = InvoiceFactory(
        appConf,
        employeeDetails(appConfPath),
        readLocalBankingDetails(appConfPath),
        readForeignBankingDetails(appConfPath)
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