package com.github.sbaldin.invoicer.processor

import com.github.sbaldin.invoicer.generator.invoice.poi.Invoice
import org.slf4j.LoggerFactory
import java.io.File

class SaveToFileProcessor(private val outputPath: String) {

    val log = LoggerFactory.getLogger(SaveToFileProcessor::class.java)

    fun process(invoices: List<Invoice>){
        invoices.forEach {
            log.info("Saving to file ${it.name}")
            it.document.write(File("${outputPath}/${it.name}").outputStream())
        }
    }
}