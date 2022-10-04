package com.guilhermeb.mymoney.viewmodel.money.file.generator.state

/**
 * Data validation state of the generate file form.
 */
data class GenerateFileFormState(
    val fileNameError: Int? = null,
    val fileExtensionError: Int? = null,
    val isFormCompleted: Boolean = false,
    val isDataValid: Boolean = false
)