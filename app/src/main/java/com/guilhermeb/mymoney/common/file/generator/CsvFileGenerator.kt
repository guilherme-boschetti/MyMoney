package com.guilhermeb.mymoney.common.file.generator

import com.guilhermeb.mymoney.MyMoneyApplication
import com.guilhermeb.mymoney.common.file.FileContent
import com.guilhermeb.mymoney.common.file.contract.FileGenerator
import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter

class CsvFileGenerator : FileGenerator {

    override fun generateFile(fileContent: FileContent): File? {
        val context = MyMoneyApplication.getInstance().applicationContext

        val fileExtension = ".csv"
        val fileName = "temp_file$fileExtension"
        val documentsDirectory = context.cacheDir
        val file = File(documentsDirectory, fileName)
        file.parentFile?.let {
            if (!it.exists()) {
                it.mkdirs()
            }
        }

        // == -- == -- == -- Add sheet content -- == -- == -- == //
        var fos: FileOutputStream? = null
        var osw: OutputStreamWriter? = null
        var bw: BufferedWriter? = null
        try {
            fos = FileOutputStream(file)
            osw = OutputStreamWriter(fos)
            bw = BufferedWriter(osw)

            val qtyColumns = fileContent.contentAttributesLabels.size

            // == -- == -- == -- Add table title -- == -- == -- == //
            fileContent.contentTitle?.let {
                val half: Int = qtyColumns / 2
                var tableTitleLine = ""
                for (i in 1 until half) {
                    tableTitleLine += ";"
                }
                tableTitleLine += it

                bw.write(tableTitleLine)
                bw.newLine()
            }
            // == -- == -- == -- Add column title -- == -- == -- == //
            var columnTitleLine = ""
            for (i in 0 until qtyColumns) {
                columnTitleLine += fileContent.contentAttributesLabels[i]
                columnTitleLine += ";"
            }
            bw.write(columnTitleLine)
            bw.newLine()
            // == -- == -- == -- Add table content -- == -- == -- == //
            for (attribute in fileContent.contentAttributesValues) {
                var contentLine = ""
                for (i in 0 until qtyColumns) {
                    contentLine += attribute[i] ?: ""
                    contentLine += ";"
                }
                bw.write(contentLine)
                bw.newLine()
            }
            // == -- == -- == -- ___ -- == -- == -- == //
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        } finally {
            bw?.close()
            osw?.close()
            fos?.close()
        }

        return file
    }
}