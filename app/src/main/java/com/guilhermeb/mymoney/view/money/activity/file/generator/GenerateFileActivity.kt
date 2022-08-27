package com.guilhermeb.mymoney.view.money.activity.file.generator

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import com.google.android.material.snackbar.Snackbar
import com.guilhermeb.mymoney.R
import com.guilhermeb.mymoney.common.constant.Constants
import com.guilhermeb.mymoney.common.enum.MoneyType
import com.guilhermeb.mymoney.common.extension.afterTextChanged
import com.guilhermeb.mymoney.common.extension.changeHintOnFocusChange
import com.guilhermeb.mymoney.common.extension.showInformationDialog
import com.guilhermeb.mymoney.common.file.FileContent
import com.guilhermeb.mymoney.common.file.generator.CsvFileGenerator
import com.guilhermeb.mymoney.common.file.generator.PdfFileGenerator
import com.guilhermeb.mymoney.common.file.generator.TxtFileGenerator
import com.guilhermeb.mymoney.common.file.generator.XlsFileGenerator
import com.guilhermeb.mymoney.common.util.DateUtil
import com.guilhermeb.mymoney.common.util.MaskUtil
import com.guilhermeb.mymoney.common.util.showToast
import com.guilhermeb.mymoney.databinding.ActivityGenerateFileBinding
import com.guilhermeb.mymoney.view.app.activity.AbstractActivity
import com.guilhermeb.mymoney.viewmodel.money.file.generator.GenerateFileViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

@AndroidEntryPoint
class GenerateFileActivity : AbstractActivity() {

    private lateinit var generateFileViewBinding: ActivityGenerateFileBinding

    @Inject
    lateinit var generateFileViewModel: GenerateFileViewModel

    private var fileDirectoryResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                changeLoadingVisibility(true)
                var uri: Uri? = null
                var showErrorMessage = false
                val data: Intent? = result.data
                data?.let {
                    uri = it.data
                    if (uri != null) {
                        val fileDescriptor =
                            applicationContext.contentResolver.openFileDescriptor(uri!!, "w")
                        if (fileDescriptor != null) {
                            val file = generateFile()
                            if (file != null && file.exists()) {
                                var outputStream: FileOutputStream? = null
                                try {
                                    outputStream = FileOutputStream(fileDescriptor.fileDescriptor)
                                    outputStream.write(file.readBytes())
                                } catch (e: Exception) {
                                    showErrorMessage = true
                                } finally {
                                    outputStream?.close()
                                }
                                file.delete()
                            } else {
                                showErrorMessage = true
                            }
                            fileDescriptor.close()
                        }
                    } else {
                        showErrorMessage = true
                    }
                }
                if (showErrorMessage) {
                    showInformationDialog(R.string.generate_file_error)
                } else {
                    generateFileSuccess(uri)
                }
                changeLoadingVisibility(false)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        generateFileViewBinding = ActivityGenerateFileBinding.inflate(layoutInflater)
        setContentView(generateFileViewBinding.root)
        setTitle(R.string.generate_file)

        initScreen()
        handleInputEvents()
        handleClicks()
        observeProperties()
    }

    private fun initScreen() {
        generateFileViewBinding.txtYearMonth.text =
            generateFileViewModel.selectedYearAndMonthName.value
    }

    private fun handleInputEvents() {
        generateFileViewBinding.edtFileName.apply {

            changeHintOnFocusChange(this@GenerateFileActivity, getString(R.string.file_name), "")

            afterTextChanged { fileName ->
                generateFileViewBinding.inFileName.error = null
                generateFileViewModel.generateFileFormDataChanged(fileName, getFileExtension())
            }
        }
    }

    private fun handleClicks() {
        generateFileViewBinding.rGrpFileExtension.setOnCheckedChangeListener { _, _ ->
            generateFileViewModel.generateFileFormDataChanged(
                generateFileViewBinding.edtFileName.text.toString(),
                getFileExtension()
            )
        }

        generateFileViewBinding.btnCancel.setOnClickListener {
            finish()
        }

        generateFileViewBinding.btnSave.setOnClickListener {
            if (generateFileViewModel.isGenerateFileFormDataValid(
                    generateFileViewBinding.edtFileName.text.toString(),
                    getFileExtension()
                )
            ) {
                val fileExtension = getFileExtension()
                val fileName = generateFileViewBinding.edtFileName.text.toString()
                val completeFileName = "$fileName$fileExtension"

                val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = getMimeType()
                    putExtra(Intent.EXTRA_TITLE, completeFileName)

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        // Optionally, specify a URI for the directory that should be opened in
                        // the system file picker before your app creates the document.
                        val uri = FileProvider.getUriForFile(
                            applicationContext,
                            applicationContext.packageName,
                            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
                        )
                        putExtra(DocumentsContract.EXTRA_INITIAL_URI, uri)
                    }
                }

                fileDirectoryResultLauncher.launch(intent)
            }
        }
    }

    private fun observeProperties() {
        generateFileViewModel.generateFileFormState.observe(this) { formState ->
            // disable save button unless form is completed
            generateFileViewBinding.btnSave.isEnabled = formState.isFormCompleted

            if (formState.fileNameError != null) {
                generateFileViewBinding.inFileName.error = getString(formState.fileNameError)
            } else {
                generateFileViewBinding.inFileName.error = null
            }
            if (formState.fileExtensionError != null) {
                showToast(this, formState.fileExtensionError)
            }
        }
    }

    private fun getMimeType(): String {
        return when (generateFileViewBinding.rGrpFileExtension.checkedRadioButtonId) {
            R.id.r_btn_csv -> Constants.CSV_MIME_TYPE
            R.id.r_btn_pdf -> Constants.PDF_MIME_TYPE
            R.id.r_btn_txt -> Constants.TXT_MIME_TYPE
            R.id.r_btn_xls -> Constants.EXCEL_MIME_TYPE
            else -> Constants.UNKNOWN_FILE_MIME_TYPE
        }
    }

    private fun getFileExtension(): String {
        return when (generateFileViewBinding.rGrpFileExtension.checkedRadioButtonId) {
            R.id.r_btn_csv -> getString(R.string.csv)
            R.id.r_btn_pdf -> getString(R.string.pdf)
            R.id.r_btn_txt -> getString(R.string.txt)
            R.id.r_btn_xls -> getString(R.string.xls)
            else -> ""
        }
    }

    private fun getFileContent(): FileContent {
        val contentTitle: String? = generateFileViewModel.selectedYearAndMonthName.value

        val contentAttributesLabels: Array<String> = arrayOf(
            getString(R.string.id),
            getString(R.string.email),
            getString(R.string.title),
            getString(R.string.description),
            getString(R.string.value),
            getString(R.string.type),
            getString(R.string.subtype),
            getString(R.string.pay_date),
            getString(R.string.paid),
            getString(R.string.fixed),
            getString(R.string.due_day),
            getString(R.string.creation_date)
        )

        val contentAttributesValues: ArrayList<Array<String?>> = ArrayList()
        val moneyItems = generateFileViewModel.moneyItems.value
        moneyItems?.let {
            for (money in moneyItems) {
                val type = if (MoneyType.INCOME.name == money.type) {
                    getString(R.string.income)
                } else if (MoneyType.EXPENSE.name == money.type) {
                    getString(R.string.expense)
                } else {
                    ""
                }
                val moneyAttributesValues: Array<String?> =
                    Array(contentAttributesLabels.size) { null }
                moneyAttributesValues[0] = money.id.toString()
                moneyAttributesValues[1] = money.userEmail
                moneyAttributesValues[2] = money.title
                moneyAttributesValues[3] = money.description
                moneyAttributesValues[4] = MaskUtil.getFormattedValueText(money.value)
                moneyAttributesValues[5] = type
                moneyAttributesValues[6] = money.subtype
                moneyAttributesValues[7] = money.payDate?.let { DateUtil.DAY_MONTH_YEAR.format(it) }
                moneyAttributesValues[8] = if (money.paid) {
                    getString(R.string.yes)
                } else {
                    getString(R.string.no)
                }
                moneyAttributesValues[9] = if (money.fixed) {
                    getString(R.string.yes)
                } else {
                    getString(R.string.no)
                }
                moneyAttributesValues[10] = money.dueDay?.toString()
                moneyAttributesValues[11] = DateUtil.DAY_MONTH_YEAR.format(money.creationDate)

                contentAttributesValues.add(moneyAttributesValues)
            }
        }

        return FileContent(contentTitle, contentAttributesLabels, contentAttributesValues)
    }

    private fun generateFile(): File? {
        return when (generateFileViewBinding.rGrpFileExtension.checkedRadioButtonId) {
            R.id.r_btn_csv -> CsvFileGenerator().generateFile(getFileContent())
            R.id.r_btn_pdf -> PdfFileGenerator().generateFile(getFileContent())
            R.id.r_btn_txt -> TxtFileGenerator().generateFile(getFileContent())
            R.id.r_btn_xls -> XlsFileGenerator().generateFile(getFileContent())
            else -> null
        }
    }

    private fun changeLoadingVisibility(visible: Boolean) {
        val visibility = if (visible) {
            View.VISIBLE
        } else {
            View.GONE
        }
        generateFileViewBinding.txtGeneratingFile.visibility = visibility
        generateFileViewBinding.progressLoading.visibility = visibility
    }

    private fun generateFileSuccess(uri: Uri?) {
        generateFileViewBinding.btnCancel.setText(R.string.back)
        showViewFileSnackBar(uri)
    }

    private fun showViewFileSnackBar(uri: Uri?) {
        val snackbar: Snackbar = Snackbar.make(
            generateFileViewBinding.root,
            R.string.view_file_snack_bar_text,
            Snackbar.LENGTH_LONG
        )
        snackbar.anchorView = generateFileViewBinding.btnCancel
        if (uri != null) {
            snackbar.setAction(R.string.view_file_snack_bar_button_text) { viewFileIntent(uri) }
        }
        snackbar.show()
    }

    private fun viewFileIntent(uri: Uri?) {
        uri?.let {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(uri, getMimeType())
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivity(intent)
        }
    }
}
