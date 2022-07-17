package com.guilhermeb.mymoney.common.extension

import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.FragmentActivity

// ========== ========== FragmentActivity Extension Functions ========== ==========

fun FragmentActivity.showSoftKeyboard() {
    if (!isSoftKeyboardVisible()) {
        val inputMethodManager =
            getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(currentFocus, 0)
    }
}

fun FragmentActivity.hideSoftKeyboard() {
    val inputMethodManager =
        getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
}

fun FragmentActivity.isSoftKeyboardVisible(): Boolean {
    return currentFocus?.let {
        ViewCompat.getRootWindowInsets(it)?.isVisible(WindowInsetsCompat.Type.ime())
            ?: false
    } ?: false
}

// == -- == -- Show and hide a view with animation -- == -- ==

fun FragmentActivity.showView(view: View) {
    val animShow: Animation = AnimationUtils.loadAnimation(this, android.R.anim.fade_in)
    view.visibility = View.VISIBLE
    view.startAnimation(animShow)
}

fun FragmentActivity.hideView(view: View) {
    val animHide: Animation = AnimationUtils.loadAnimation(this, android.R.anim.fade_out)
    animHide.setAnimationListener(object : Animation.AnimationListener {
        override fun onAnimationStart(animation: Animation?) {
            // do nothing
        }

        override fun onAnimationEnd(animation: Animation?) {
            view.visibility = View.GONE
        }

        override fun onAnimationRepeat(animation: Animation?) {
            // do nothing
        }
    })
    view.startAnimation(animHide)
}

// == -- == -- == -- == -- ==