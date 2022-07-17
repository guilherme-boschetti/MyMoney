package com.guilhermeb.mymoney.view.login.dialog

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ScrollView
import androidx.appcompat.app.AlertDialog
import com.guilhermeb.mymoney.databinding.DialogTermsAndConditionsBinding

class TermsAndConditionsDialog(
    val context: Context,
    private val callback: TermsAndConditionsCallback
) {

    private lateinit var termsAndConditionsViewBinding: DialogTermsAndConditionsBinding
    private lateinit var dialog: AlertDialog

    fun openDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        termsAndConditionsViewBinding =
            DialogTermsAndConditionsBinding.inflate(LayoutInflater.from(context))
        addListeners()
        termsAndConditionsViewBinding.scrollView.post {
            if (canScroll(termsAndConditionsViewBinding.scrollView)) {
                termsAndConditionsViewBinding.btnAcceptTermsAndConditions.isEnabled = false
            }
        }
        builder.setView(termsAndConditionsViewBinding.root)
        dialog = builder.create()
        dialog.show()
    }

    private fun addListeners() {
        termsAndConditionsViewBinding.btnAcceptTermsAndConditions.setOnClickListener {
            dialog.dismiss()
            callback.termsAndConditionsAccepted()
        }
        termsAndConditionsViewBinding.imgBtnClose.setOnClickListener {
            dialog.dismiss()
        }
        val scroll: ScrollView = termsAndConditionsViewBinding.scrollView
        scroll.viewTreeObserver.addOnScrollChangedListener {
            if (scroll.getChildAt(0).bottom <= scroll.height + scroll.scrollY) {
                //scroll view is at bottom
                termsAndConditionsViewBinding.btnAcceptTermsAndConditions.isEnabled = true
            }
        }
    }

    private fun canScroll(scroll: ScrollView): Boolean {
        val child: View = scroll.getChildAt(0)
        val childHeight = child.height
        return scroll.height < childHeight + scroll.paddingTop + scroll.paddingBottom
    }

    interface TermsAndConditionsCallback {
        fun termsAndConditionsAccepted()
    }
}