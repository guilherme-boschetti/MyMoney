package com.guilhermeb.mymoney.common.component

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.annotation.StringRes
import com.guilhermeb.mymoney.R

class CustomProgressDialog(context: Context, message: String?, cancelable: Boolean) :
    Dialog(context, R.style.CustomProgressDialog) {

    constructor(context: Context, @StringRes messageResource: Int, cancelable: Boolean) : this(
        context,
        context.getString(messageResource),
        cancelable
    )

    init {
        val windowAttributes = window!!.attributes
        windowAttributes.gravity = Gravity.CENTER_HORIZONTAL
        window!!.attributes = windowAttributes
        setTitle(message)
        setCancelable(cancelable)
        setOnCancelListener(null)
        val layout = LinearLayout(context)
        layout.orientation = LinearLayout.VERTICAL
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        val progressBar = ProgressBar(context)
        layout.addView(progressBar, params)
        addContentView(layout, params)
    }
}