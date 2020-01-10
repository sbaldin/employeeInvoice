package com.github.sbaldin.invoicer.domain

data class AppConf(
    val appRunType: AppRunTypeEnum,
    val resultFileType: ResultFileTypeEnum,
    val outputPath: String,
    val sendToEmail: String
)
