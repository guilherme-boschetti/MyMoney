package com.guilhermeb.mymoney.common.validation

import org.junit.Assert.*
import org.junit.Test

class EmailValidationTest {

    @Test
    fun emailValidation_CorrectEmail_ReturnsTrue() {
        assertTrue(isEmailValid("name@email.com"))
    }

    @Test
    fun emailValidation_IncorrectEmail_ReturnsFalse() {
        assertFalse(isEmailValid("name@email"))
    }

    @Test
    fun emailValidation_IncorrectEmailWithoutAt_ReturnsFalse() {
        assertFalse(isEmailValid("name_email.com"))
    }

    @Test
    fun emailValidation_EmptyEmail_ReturnsFalse() {
        assertFalse(isEmailValid(""))
    }
}