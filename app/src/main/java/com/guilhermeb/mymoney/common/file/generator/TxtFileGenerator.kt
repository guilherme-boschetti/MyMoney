package com.guilhermeb.mymoney.common.file.generator

import com.guilhermeb.mymoney.MyMoneyApplication
import com.guilhermeb.mymoney.common.file.FileContent
import com.guilhermeb.mymoney.common.file.contract.FileGenerator
import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter

class TxtFileGenerator : FileGenerator {

    override fun generateFile(fileContent: FileContent): File? {
        val context = MyMoneyApplication.getInstance().applicationContext

        val fileExtension = ".txt"
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

            // == -- == -- == -- Add title -- == -- == -- == //
            fileContent.contentTitle?.let {
                bw.write(it)
                bw.newLine()
                bw.newLine()
            }
            // == -- == -- == -- Add content -- == -- == -- == //
            for (attribute in fileContent.contentAttributesValues) {
                for (i in 0 until qtyColumns) {
                    var contentLine = fileContent.contentAttributesLabels[i]
                    contentLine += ": "
                    contentLine += attribute[i] ?: ""
                    bw.write(contentLine)
                    bw.newLine()
                }
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