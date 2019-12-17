package com.github.sbaldin.invoicer.generator.invoice

import org.apache.poi.POIXMLDocument

interface InvoiceGenerator {
    fun generate(): Invoice
}

data class Invoice(
    val name: String,
    val document: POIXMLDocument
)