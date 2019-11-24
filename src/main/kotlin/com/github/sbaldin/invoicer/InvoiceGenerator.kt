package com.github.sbaldin.invoicer

import org.apache.poi.ss.util.CellRangeAddress
import org.apache.poi.xssf.usermodel.XSSFWorkbook


class InvoiceGenerator(
   val appConf: AppConf,
   val employee: Employee,
   val bankingDetails: BankingDetails
) {
    fun generate(){
        when(appConf.runType){
            RunTypeEnum.Banking -> generateBanking()
            RunTypeEnum.Employeer -> generateEmployeer()
            RunTypeEnum.Both -> generateBoth()
        }
        val workbook = XSSFWorkbook().also {
            val sheet = it.createSheet("Sheet").also { xssfSheet ->
                xssfSheet.createRow(1).apply {
                    createCell(1).apply {
                        setCellValue("${employee.name} RiskMatch Invoice")
                    }
                }
                xssfSheet.addMergedRegion(CellRangeAddress(1, 1, 1, 2))
            }

        }
    }

    private fun generateBoth() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun generateEmployeer() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun generateBanking() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}


