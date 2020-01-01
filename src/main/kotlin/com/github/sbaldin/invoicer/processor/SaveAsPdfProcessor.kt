package com.github.sbaldin.invoicer.processor

import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.TimeUnit

val log = LoggerFactory.getLogger(SaveAsPdfProcessor::class.java)

class SaveAsPdfProcessor(
    val binaryPath: String,
    val jsPath: String
) {

    fun process(
        htmlResourcePath: String,
        saveToPdfPath: String
    ) {
        try {
            val binary = File(binaryPath)
            val js = File(jsPath)
            val tempFile = File("./invoice.html")
            val savePdf = File(saveToPdfPath)
            FileOutputStream(tempFile).write(SaveAsPdfProcessor::class.java.classLoader.getResource(htmlResourcePath)!!.readBytes())

            val cmd = arrayOf(
                binary.canonicalPath,
                js.canonicalPath,
                tempFile.canonicalPath,
                savePdf.canonicalPath
            )
            log.info(cmd.joinToString())
            val proc = Runtime.getRuntime().exec(cmd)

            proc.waitFor(20, TimeUnit.SECONDS)
        } catch (e: Exception) {
            log.error("Error during saving invoice as pdf", e)
        }
    }
}