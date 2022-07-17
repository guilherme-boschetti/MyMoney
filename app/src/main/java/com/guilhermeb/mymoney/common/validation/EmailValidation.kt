package com.guilhermeb.mymoney.common.validation

import android.util.Patterns

/**
 * E-mail validation.
 */
fun isEmailValid(email: String): Boolean {
    return if (email.contains('@')) {
        Patterns.EMAIL_ADDRESS.matcher(email).matches()
    } else {
        false
    }
}