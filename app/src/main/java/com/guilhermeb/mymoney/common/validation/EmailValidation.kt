package com.guilhermeb.mymoney.common.validation

import androidx.core.util.PatternsCompat

/**
 * E-mail validation.
 */
fun isEmailValid(email: String): Boolean {
    return if (email.contains('@')) {
        PatternsCompat.EMAIL_ADDRESS.matcher(email).matches()
    } else {
        false
    }
}