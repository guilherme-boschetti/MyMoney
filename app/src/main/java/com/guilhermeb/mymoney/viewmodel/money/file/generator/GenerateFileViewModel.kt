package com.guilhermeb.mymoney.viewmodel.money.file.generator

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.guilhermeb.mymoney.R
import com.guilhermeb.mymoney.model.data.local.room.entity.money.Money
import com.guilhermeb.mymoney.viewmodel.money.MoneyViewModel
import com.guilhermeb.mymoney.viewmodel.money.file.generator.state.GenerateFileFormState
import java.util.regex.Pattern
import javax.inject.Inject

class GenerateFileViewModel @Inject constructor(private val moneyViewModel: MoneyViewModel) :
    ViewModel() {

    // == -- Money == -- == -- == -- == -- == -- == -- == -- == -- == -- == -- == -- == -- ==

    val selectedYearAndMonthName: LiveData<String> get() = moneyViewModel.selectedYearAndMonthName

    val moneyItems: LiveData<List<Money>> get() = moneyViewModel.moneyItems

    // == -- Generate File == -- == -- == -- == -- == -- == -- == -- == -- == -- == -- == -- == -- ==

    private val _generateFileFormState = MutableLiveData<GenerateFileFormState>()
    val generateFileFormState: LiveData<GenerateFileFormState> = _generateFileFormState

    fun generateFileFormDataChanged(fileName: String, fileExtension: String) {
        val isFormFilled = fileName.isNotEmpty() && fileExtension.isNotEmpty()
        _generateFileFormState.value = GenerateFileFormState(isFormCompleted = isFormFilled)
    }

    fun isGenerateFileFormDataValid(fileName: String, fileExtension: String): Boolean {
        if (fileName.isEmpty()) {
            _generateFileFormState.value =
                GenerateFileFormState(
                    isFormCompleted = false,
                    fileNameError = R.string.file_name_should_not_be_empty
                )
        } else if (!isAlphaNumeric(fileName)) {
            _generateFileFormState.value =
                GenerateFileFormState(
                    isFormCompleted = false,
                    fileNameError = R.string.invalid_file_name
                )
        } else if (fileExtension.isEmpty()) {
            _generateFileFormState.value =
                GenerateFileFormState(
                    isFormCompleted = false,
                    fileExtensionError = R.string.file_extension_required
                )
        } else {
            _generateFileFormState.value =
                GenerateFileFormState(isFormCompleted = true, isDataValid = true)
            return true
        }
        return false
    }

    private val p: Pattern = Pattern.compile("^[a-zA-Z0-9]*$")

    private fun isAlphaNumeric(s: String): Boolean {
        return p.matcher(s).find()
    }
}