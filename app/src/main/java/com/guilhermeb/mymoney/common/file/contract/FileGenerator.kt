package com.guilhermeb.mymoney.common.file.contract

import com.guilhermeb.mymoney.common.file.FileContent
import java.io.File

interface FileGenerator {
    fun generateFile(fileContent: FileContent): File?
}