package com.guilhermeb.mymoney.common.file.generator

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import com.guilhermeb.mymoney.MyMoneyApplication
import com.guilhermeb.mymoney.common.file.FileContent
import com.guilhermeb.mymoney.common.file.contract.FileGenerator
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class PdfFileGenerator : FileGenerator {

    override fun generateFile(fileContent: FileContent): File? {
        val context = MyMoneyApplication.getInstance().applicationContext

        val fileExtension = ".pdf"
        val fileName = "temp_file$fileExtension"
        val documentsDirectory = context.cacheDir
        val file = File(documentsDirectory, fileName)
        file.parentFile?.let {
            if (!it.exists()) {
                it.mkdirs()
            }
        }

        // create a new document
        val document = PdfDocument()

        // create a page description
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 size

        // add pages
        for (attribute in fileContent.contentAttributesValues) {

            // start a page
            val page = document.startPage(pageInfo)

            // draw content on the page
            drawContentOnThePage(page.canvas, fileContent, attribute)

            // finish the page
            document.finishPage(page)
        }

        try {
            // write the document content to file
            document.writeTo(FileOutputStream(file))
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }

        // close the document
        document.close()

        return file
    }

    private fun drawContentOnThePage(
        canvas: Canvas,
        fileContent: FileContent,
        attribute: Array<String?>
    ) {
        var paint = Paint()

        paint.color = Color.BLACK
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 1f

        canvas.drawRect(25f, 25f, 570f, 817f, paint)

        paint = Paint()
        paint.color = Color.BLACK
        paint.textSize = 12f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)

        canvas.drawText(fileContent.contentTitle ?: "", 100f, 50f, paint)

        paint = Paint()
        paint.color = Color.BLACK
        paint.textSize = 12f

        val qtyColumns = fileContent.contentAttributesLabels.size
        var yPosition = 95f

        for (i in 0 until qtyColumns) {
            var contentLine = fileContent.contentAttributesLabels[i]
            contentLine += ": "
            contentLine += attribute[i] ?: ""

            canvas.drawText(contentLine, 50f, yPosition, paint)
            yPosition += 20f
        }
    }
}