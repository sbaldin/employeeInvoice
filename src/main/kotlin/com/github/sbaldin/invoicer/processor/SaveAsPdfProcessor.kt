package com.github.sbaldin.invoicer.processor

import com.github.sbaldin.invoicer.generator.invoice.PdfInvoice
import org.slf4j.LoggerFactory
import java.io.File
import java.lang.UnsupportedOperationException
import java.util.concurrent.TimeUnit

val log = LoggerFactory.getLogger(SaveAsPdfProcessor::class.java)

class SaveAsPdfProcessor(
    outputPath: String,
    private val binaryPath: String = HeadlessChromePathProvider().get(),
    private val jsPath: String = JsLibProvider().get()
) : ResultFileProcessor<PdfInvoice>(outputPath) {

    override fun process(invoices: List<PdfInvoice>) {
        invoices.forEach {
            try {
                val binary = File(binaryPath)
                val js = File(jsPath)
                val savePdf = File(outputPath + it.name)

                val cmd = arrayOf(
                    binary.canonicalPath,
                    js.canonicalPath,
                    it.document.canonicalPath,
                    savePdf.canonicalPath
                )
                log.info("Following command will be executed: \n ${cmd.joinToString()}")
                val proc = Runtime.getRuntime().exec(cmd)
                proc.waitFor(20, TimeUnit.SECONDS)
            } catch (e: Exception) {
                log.error("Error during saving invoice as pdf", e)
            } finally {
                it.document.delete()
            }
        }
    }
}

class HeadlessChromePathProvider {

    fun get(): String {
        val os = System.getProperty("os.name").toLowerCase()
        val binaryName = when {
            os.contains("linux") -> "phantomjs_lin64"
            os.contains("mac") -> "phantomjs_mac"
            os.contains("win") -> throw UnsupportedOperationException("There no binary for windows, please create PR if you have it.")
            else -> "phantomjs_lin64"
        }
        return "./binaries/$binaryName"
    }
}

class JsLibProvider {
    fun get(): String {
        return "./binaries/rasterize.js"
    }
}