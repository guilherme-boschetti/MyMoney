package com.guilhermeb.mymoney.common.cryptography

import java.security.MessageDigest

fun md5(s: String?): String {
    val md5 = "MD5"
    try {
        // Create MD5 Hash
        val digest = MessageDigest
            .getInstance(md5)
        digest.update((s ?: "").toByteArray())
        val messageDigest = digest.digest()

        // Create Hex String
        val hexString = StringBuilder()
        for (aMessageDigest in messageDigest) {
            var h = Integer.toHexString(0xFF and aMessageDigest.toInt())
            while (h.length < 2) h = "0$h"
            hexString.append(h)
        }
        return hexString.toString()
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return ""
}