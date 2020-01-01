package com.github.sbaldin.invoicer

import com.github.sbaldin.invoicer.domain.*
import com.github.sbaldin.invoicer.generator.InvoiceFactory
import com.github.sbaldin.invoicer.processor.SaveAsPdfProcessor
import com.github.sbaldin.invoicer.processor.SaveToFileProcessor
import com.uchuhimo.konf.Config
import com.uchuhimo.konf.source.yaml
import com.uchuhimo.konf.toValue
import javafx.application.Application
import org.slf4j.LoggerFactory


val log = LoggerFactory.getLogger(Application::class.java)

private fun readAppConf(appConfPath: String, resourcePath: String ="application.yaml") =
    Config().from.yaml.file(appConfPath)
        .from.yaml.resource(resourcePath)
        .at("app").toValue<AppConf>()

private fun readEmployee(appConfPath: String, resourcePath: String = "application.yaml") =
    Config().from.yaml.file(appConfPath)
        .from.yaml.resource(resourcePath)
        .at("employee").toValue<EmployeeDetails>()

private fun readLocalBankingDetails(appConfPath: String, resourcePath: String ="application.yaml") =
    Config().from.yaml.file(appConfPath)
        .from.yaml.resource(resourcePath)
        .at("banking").at("local").toValue<LocalBankingDetails>()

private fun readForeignBankingDetails(appConfPath: String, resourcePath: String ="application.yaml") =
    Config().from.yaml.file(appConfPath)
        .from.yaml.resource(resourcePath)
        .at("banking").at("foreign").toValue<ForeignBankingDetails>()

fun main(args: Array<String>) {
    log.info("Args:" + args.joinToString())
    val appConfPath = args[0]
    log.info("Application config path:$appConfPath")

    val appConf = readAppConf(appConfPath)
    val invoiceFactory = InvoiceFactory(
        appConf,
        readEmployee(appConfPath),
        readLocalBankingDetails(appConfPath),
        readForeignBankingDetails(appConfPath)
    )

    val invoices = invoiceFactory.getOfficeInvoiceList(RunTypeEnum.Both)
    SaveToFileProcessor(appConf.outputPath).process(invoices = invoices)
    SaveAsPdfProcessor("./phantomjs_lin64", "./rasterize.js").process("invoice_foreign.html",appConf.outputPath + "/invoice_foreign.pdf")
    log.info("Invoices generation finished.")
}