package com.github.sbaldin.invoicer

import com.github.sbaldin.invoicer.domain.*
import com.uchuhimo.konf.Config
import com.uchuhimo.konf.source.yaml
import com.uchuhimo.konf.toValue
import javafx.application.Application
import org.slf4j.LoggerFactory


val log = LoggerFactory.getLogger(Application::class.java)

fun main(args: Array<String>) {
    log.info("Args:" + args.joinToString())
    val appConfPath = args[0]
    log.info("Application config path:$appConfPath")

    InvoiceGenerator(
        readAppConf(appConfPath),
        readEmployee(appConfPath),
        readLocalBankingDetails(appConfPath),
        readForeignBankingDetails(appConfPath)).apply {
        generate()
    }
}

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

data class AppContext(
    val appConf: AppConf,
    val employeeDetails: EmployeeDetails,
    val localBankingDetails: LocalBankingDetails,
    val foreignBankingDetails: ForeignBankingDetails
)