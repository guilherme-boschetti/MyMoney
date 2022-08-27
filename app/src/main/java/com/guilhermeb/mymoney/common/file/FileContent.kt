package com.guilhermeb.mymoney.common.file

data class FileContent(
    val contentTitle: String? = null,
    val contentAttributesLabels: Array<String>,
    val contentAttributesValues: ArrayList<Array<String?>>
)