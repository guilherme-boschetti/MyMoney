package com.guilhermeb.mymoney.common.cryptography

import org.junit.Assert.*
import org.junit.Test

class MD5Test {

    @Test
    fun MD5_Input_ReturnsValidMd5Value() {
        assertEquals(
            md5("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"),
            "d174ab98d277d9f5a5611c2c9f419d9f"
        )
    }

    @Test
    fun MD5_EmptyInput_ReturnsValidMd5Value() {
        assertEquals(md5(""), "d41d8cd98f00b204e9800998ecf8427e")
    }
}