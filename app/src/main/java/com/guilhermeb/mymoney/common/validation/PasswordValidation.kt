package com.guilhermeb.mymoney.common.validation

/**
 * Password validation.
 */
fun isPasswordValid(password: String): Boolean {
    return password.length > 0
}