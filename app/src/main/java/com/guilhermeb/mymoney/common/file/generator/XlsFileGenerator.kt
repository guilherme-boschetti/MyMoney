package com.guilhermeb.mymoney.common.file.generator

import com.guilhermeb.mymoney.MyMoneyApplication
import com.guilhermeb.mymoney.R
import com.guilhermeb.mymoney.common.file.FileContent
import com.guilhermeb.mymoney.common.file.contract.FileGenerator
import jxl.Workbook
import jxl.format.*
import jxl.format.Alignment
import jxl.format.Border
import jxl.format.BorderLineStyle
import jxl.format.Colour
import jxl.write.*
import jxl.write.WritableFont.*
import java.io.File

class XlsFileGenerator : FileGenerator {

    override fun generateFile(fileContent: FileContent): File? {
        val context = MyMoneyApplication.getInstance().applicationContext

        val fileExtension = ".xls"
        val fileName = "temp_file$fileExtension"
        val documentsDirectory = context.cacheDir
        val file = File(documentsDirectory, fileName)
        file.parentFile?.let {
            if (!it.exists()) {
                it.mkdirs()
            }
        }
        val wb: WritableWorkbook?
        try {
            wb = Workbook.createWorkbook(file)
        } catch (e: Exception) { // IOException
            e.printStackTrace()
            return null
        }
        if (wb != null) {
            wb.createSheet(context.getString(R.string.sheet), 0)
            val sheet = wb.getSheet(0)

            // == -- == -- == -- Add sheet content -- == -- == -- == //
            @Suppress("INACCESSIBLE_TYPE")
            try {
                val qtyColumns = fileContent.contentAttributesLabels.size
                var line = 0

                // == -- == -- == -- Add table title -- == -- == -- == //

                // Change the format of cell
                val boldName = WritableFont(ARIAL, DEFAULT_POINT_SIZE, BOLD) // Define the font
                val cfName = WritableCellFormat(boldName)
                cfName.setBackground(Colour.LIGHT_GREEN) // cell background color
                // set cell borders
                cfName.setBorder(Border.BOTTOM, BorderLineStyle.THIN)
                cfName.setBorder(Border.LEFT, BorderLineStyle.THIN)
                cfName.setBorder(Border.RIGHT, BorderLineStyle.THIN)
                cfName.setBorder(Border.TOP, BorderLineStyle.THIN)
                cfName.alignment = Alignment.CENTRE
                sheet.mergeCells(0, line, qtyColumns - 1, line)
                putLabel(sheet, line, 0, fileContent.contentTitle ?: "", cfName) // Add cell

                // == -- == -- == -- Add column title -- == -- == -- == //
                line++

                // Change the format of cell
                val bold = WritableFont(ARIAL, DEFAULT_POINT_SIZE, BOLD) // Define the font
                val cf = WritableCellFormat(bold)
                cf.setBackground(Colour.GREY_25_PERCENT) // cell background color
                // set cell borders
                cf.setBorder(Border.BOTTOM, BorderLineStyle.THIN)
                cf.setBorder(Border.LEFT, BorderLineStyle.THIN)
                cf.setBorder(Border.RIGHT, BorderLineStyle.THIN)
                cf.setBorder(Border.TOP, BorderLineStyle.THIN)
                for (i in 0 until qtyColumns) {
                    putLabel(sheet, line, i, fileContent.contentAttributesLabels[i], cf) // Add cel
                }

                // == -- == -- == -- Add table content -- == -- == -- == //
                line++
                for (attribute in fileContent.contentAttributesValues) {
                    for (i in 0 until qtyColumns) {
                        putLabel(sheet, line, i, attribute[i] ?: "")
                    }
                    line++
                }

                // == -- == -- == -- Auto size columns width according the content -- == -- == -- == //
                setAutoSizeColumns(sheet, qtyColumns)
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }

            // == -- == -- == -- ___ -- == -- == -- == //
            try {
                wb.write()
                wb.close()
            } catch (e: Exception) { // IOException
                e.printStackTrace()
                return null
            }
        }
        return file
    }

    private fun putLabel(sheet: WritableSheet, line: Int, column: Int, content: String) {
        val cf = WritableCellFormat()
        try {
            cf.setBorder(Border.BOTTOM, BorderLineStyle.THIN)
            cf.setBorder(Border.LEFT, BorderLineStyle.THIN)
            cf.setBorder(Border.RIGHT, BorderLineStyle.THIN)
            cf.setBorder(Border.TOP, BorderLineStyle.THIN)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        putLabel(sheet, line, column, content, cf)
    }

    private fun putLabel(
        sheet: WritableSheet, line: Int, column: Int, content: String, cellFormat: CellFormat?
    ) {
        val label: Label = if (cellFormat != null) {
            Label(column, line, content, cellFormat)
        } else {
            Label(column, line, content)
        }
        try {
            sheet.addCell(label)
        } catch (e: Exception) { // RowsExceededException // WriteException
            e.printStackTrace()
        }
    }

    private fun setAutoSizeColumns(sheet: WritableSheet, qtyColumns: Int) {
        for (i in 0 until qtyColumns) {
            val cell = sheet.getColumnView(i)
            cell.isAutosize = true
            sheet.setColumnView(i, cell)
        }
    }
}