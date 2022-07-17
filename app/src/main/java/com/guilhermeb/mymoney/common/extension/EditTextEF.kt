package com.guilhermeb.mymoney.common.extension

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.fragment.app.FragmentActivity

// ========== ========== EditText Extension Functions ========== ==========

/**
 * Extension function to simplify change the hint of an EditText when focus change.
 */
fun EditText.changeHintOnFocusChange(
    activity: FragmentActivity?,
    hintHasFocus: String,
    hintNotHasFocus: String
) {
    this.setOnFocusChangeListener { _, hasFocus ->
        if (hasFocus) {
            this.hint = hintHasFocus
            activity?.showSoftKeyboard()
        } else {
            this.hint = hintNotHasFocus
        }
    }
}

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            // do nothing
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            // do nothing
        }
    })
}