package com.github.sbaldin.invoicer

import com.github.sbaldin.invoicer.model.AppConf
import com.github.sbaldin.invoicer.model.BankingDetails
import com.github.sbaldin.invoicer.model.EmployeeDetails
import com.uchuhimo.konf.Config
import com.uchuhimo.konf.source.yaml
import com.uchuhimo.konf.toValue
import javafx.application.Application
import org.slf4j.LoggerFactory


val log = LoggerFactory.getLogger(Application::class.java)

fun main(args: Array<String>) {
    log.info("Args:" + args.joinToString())
    log.info("Working Directory = " +
            System.getProperty("user.dir"));
    val appConfPath = args[0]
    val configs = ConfigHolder(readAppConf(appConfPath), readEmployee(appConfPath), readBankingDetails(appConfPath))
    log.info("Configs was loaded.")
    InvoiceGenerator(configs.appConf, configs.employeeDetails, configs.bankingDetails).apply {
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

private fun readBankingDetails(appConfPath: String, resourcePath: String ="application.yaml") =
    Config().from.yaml.file(appConfPath)
            .from.yaml.resource(resourcePath)
            .at("banking").toValue<BankingDetails>()

data class ConfigHolder(
    val appConf: AppConf,
    val employeeDetails: EmployeeDetails,
    val bankingDetails: BankingDetails
)