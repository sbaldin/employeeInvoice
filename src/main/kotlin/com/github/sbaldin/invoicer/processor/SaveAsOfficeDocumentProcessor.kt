package com.github.sbaldin.invoicer.processor

import com.github.sbaldin.invoicer.generator.invoice.PoiInvoice
import org.slf4j.LoggerFactory
import java.io.File

class SaveAsOfficeDocumentProcessor(outputPath: String) : ResultFileProcessor<PoiInvoice>(outputPath) {

    val log = LoggerFactory.getLogger(SaveAsOfficeDocumentProcessor::class.java)

    override fun process(invoices: List<PoiInvoice>){
        invoices.forEach {
            log.info("Saving to file ${it.name}")
            it.document.write(File("${outputPath}/${it.name}").outputStream())
        }
    }
}