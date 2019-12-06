package com.github.sbaldin.invoicer.domain

data class AppConf(
    val runType: RunTypeEnum,
    val foreignTemplatePath: String,
    val outputPath: String,
    val sendToEmail: String
)
