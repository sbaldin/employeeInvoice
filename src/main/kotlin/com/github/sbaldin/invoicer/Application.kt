package com.github.sbaldin.invoicer

import com.uchuhimo.konf.Config
import com.uchuhimo.konf.source.yaml
import com.uchuhimo.konf.toValue
import javafx.application.Application
import org.slf4j.LoggerFactory

val log = LoggerFactory.getLogger(Application::class.java)

fun main(args: Array<String>) {
    log.info("args:" + args.joinToString())
    val appConfPath = args[0]
    val appConf = readAppConf(appConfPath)
    val employee = readEmployee(appConfPath)
    val bankingDetails = readBankingDetails(appConfPath)
    log.info("Сonfigs was loaded.")
    InvoiceGenerator(appConf, employee, bankingDetails).apply {
            generate()
    }
}


private fun readAppConf(appConfPath: String) =
    Config().from.yaml.file(appConfPath).at("app").toValue<AppConf>()

private fun readEmployee(appConfPath: String) =
    Config().from.yaml.file(appConfPath).at("employee").toValue<Employee>()

private fun readBankingDetails(appConfPath: String) =
    Config().from.yaml.file(appConfPath).at("bankingDetails").toValue<BankingDetails>()