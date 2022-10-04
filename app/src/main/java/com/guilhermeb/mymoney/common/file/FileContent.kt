package com.guilhermeb.mymoney.common.file

data class FileContent(
    val contentTitle: String? = null,
    val contentAttributesLabels: ArrayList<String>,
    val contentAttributesValues: ArrayList<Array<String?>>
)