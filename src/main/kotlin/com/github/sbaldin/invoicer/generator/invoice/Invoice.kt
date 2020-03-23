package com.github.sbaldin.invoicer.generator.invoice

import org.apache.poi.POIXMLDocument
import java.io.File

interface InvoiceGenerator {
    fun generate(): Invoice
}

interface Invoice {
    val name: String
}

class PoiInvoice(
    val document: POIXMLDocument,
    override val name: String
) : Invoice

class PdfInvoice(
    val document: File,
    override val name: String
) : Invoice