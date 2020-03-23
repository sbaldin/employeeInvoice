package com.github.sbaldin.invoicer.processor

import com.github.sbaldin.invoicer.generator.invoice.Invoice

abstract class ResultFileProcessor<T : Invoice>(val outputPath: String) {
    abstract fun process(invoices: List<T>)
}
