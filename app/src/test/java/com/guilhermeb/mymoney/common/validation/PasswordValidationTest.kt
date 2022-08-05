package com.guilhermeb.mymoney.common.validation

import org.junit.Assert.*
import org.junit.Test

class PasswordValidationTest {

    @Test
    fun passwordValidation_CorrectPasswordNumber_ReturnsTrue() {
        assertTrue(isPasswordValid("1"))
    }

    @Test
    fun passwordValidation_CorrectPasswordLetter_ReturnsTrue() {
        assertTrue(isPasswordValid("a"))
    }

    @Test
    fun passwordValidation_CorrectPasswordCharacter_ReturnsTrue() {
        assertTrue(isPasswordValid("_"))
    }

    @Test
    fun passwordValidation_EmptyPassword_ReturnsFalse() {
        assertFalse(isPasswordValid(""))
    }
}