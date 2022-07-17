package com.guilhermeb.mymoney.common.util

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes

fun showToast(context: Context, @StringRes messageResId: Int, durationLong: Boolean) {
    if (durationLong)
        Toast.makeText(context, messageResId, Toast.LENGTH_LONG).show()
    else
        Toast.makeText(context, messageResId, Toast.LENGTH_SHORT).show()
}

fun showToast(context: Context, message: String, durationLong: Boolean) {
    if (durationLong)
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    else
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

fun showToast(context: Context, @StringRes messageResId: Int) {
    showToast(context, messageResId, true)
}

fun showToast(context: Context, message: String) {
    showToast(context, message, true)
}