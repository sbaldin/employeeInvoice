import com.github.sbaldin.invoicer.domain.AppRunTypeEnum
import com.github.sbaldin.invoicer.domain.ResultFileTypeEnum
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

fun createTempYamlConfig(runType: AppRunTypeEnum, fileType: ResultFileTypeEnum): File {
    return File.createTempFile("office", ".yaml").apply {
        writer().apply {
            write("app:\n")
            write("    appRunType: $runType #FOREIGN_BANK_INVOICE #LOCAL_BANK_INVOICE\n")
            write("    resultFileType: $fileType #OFFICE #PDF\n")
            write("    outputPath: / #relative path from user home directory\n")
        }.close()
    }
}

fun getSDF(pattern: String, locale: Locale?) = SimpleDateFormat(pattern, locale).apply {
    timeZone = TimeZone.getTimeZone("GMT")
}