package com.github.sbaldin.invoicer.generator.invoice.pdf

import com.github.sbaldin.invoicer.domain.EmployeeDetailsModel
import com.github.sbaldin.invoicer.domain.ForeignBankingModel
import com.github.sbaldin.invoicer.domain.LocalBankingModel
import freemarker.template.Configuration
import freemarker.template.Template
import freemarker.template.TemplateExceptionHandler
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import kotlin.random.Random

abstract class AbstractPdfBankInvoice(
    protected open val employeeDetails: EmployeeDetailsModel,
    protected open val localBankingModel: LocalBankingModel,
    protected open val foreignBankingModel: ForeignBankingModel,
    protected open val templatePath: String
) {

    open fun generatePdfFile(): File {
        // Create your Configuration instance, and specify if up to what FreeMarker
        // version (here 2.3.29) do you want to apply the fixes that are not 100%
        // backward-compatible. See the Configuration JavaDoc for details.
        val cfg = Configuration()

        // Specify the source where the template files come from. Here I set a
        // plain directory for it, but non-file-system sources are possible too:

        // From here we will set the settings recommended for new projects. These
        // aren't the defaults for backward compatibilty.

        // Set the preferred charset template files are stored in. UTF-8 is
        // a good choice in most applications:
        cfg.defaultEncoding = "UTF-8"

        // Sets how errors will appear.
        // During web page *development* TemplateExceptionHandler.HTML_DEBUG_HANDLER is better.
        cfg.templateExceptionHandler = TemplateExceptionHandler.RETHROW_HANDLER
        cfg.setClassForTemplateLoading(javaClass, "/")

        /* Create a data-model */
        val root = gePlaceholderModel()

        /* Get the template (uses cache internally) */
        val temp: Template = cfg.getTemplate(templatePath)

        /* Merge data-model with template */
        val htmlFilePath = "./filled_template${Random.nextInt()}.html"
        val invoiceFile = File(htmlFilePath)
        val out = OutputStreamWriter(FileOutputStream(invoiceFile))
        temp.process(root, out)
        // Note: Depending on what `out` is, you may need to call `out.close()`.
        // This is usually the case for file output, but not for servlet output.
        out.close()
        return invoiceFile
    }

    abstract fun gePlaceholderModel(): HashMap<String, Any?>
}