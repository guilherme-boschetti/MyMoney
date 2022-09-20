package com.guilhermeb.mymoney.common.util

import android.content.Context
import com.guilhermeb.mymoney.R
import com.guilhermeb.mymoney.common.constant.Constants
import com.guilhermeb.mymoney.common.helper.SharedPreferencesHelper
import com.guilhermeb.mymoney.common.helper.getSharedPreferencesKey

enum class Themes {
    GREEN, RED, BLUE, PINK, PURPLE
}

fun saveTheme(theme: Themes) {
    SharedPreferencesHelper.getInstance()
        .setValue(getSharedPreferencesKey(Constants.CURRENT_THEME), theme.name)
}

fun getSelectedTheme(noActionBar: Boolean): Int {
    val savedTheme = SharedPreferencesHelper.getInstance()
        .getValue(getSharedPreferencesKey(Constants.CURRENT_THEME), Themes.GREEN.name)

    return if (noActionBar) {
        when (savedTheme) {
            Themes.GREEN.name -> R.style.Theme_MyMoney_Green_NoActionBar
            Themes.RED.name -> R.style.Theme_MyMoney_Red_NoActionBar
            Themes.BLUE.name -> R.style.Theme_MyMoney_Blue_NoActionBar
            Themes.PINK.name -> R.style.Theme_MyMoney_Pink_NoActionBar
            Themes.PURPLE.name -> R.style.Theme_MyMoney_Purple_NoActionBar
            else -> R.style.Theme_MyMoney_Green_NoActionBar
        }
    } else {
        when (savedTheme) {
            Themes.GREEN.name -> R.style.Theme_MyMoney_Green
            Themes.RED.name -> R.style.Theme_MyMoney_Red
            Themes.BLUE.name -> R.style.Theme_MyMoney_Blue
            Themes.PINK.name -> R.style.Theme_MyMoney_Pink
            Themes.PURPLE.name -> R.style.Theme_MyMoney_Purple
            else -> R.style.Theme_MyMoney_Green
        }
    }
}

fun getColorPrimaryResId(context: Context): Int {
    val ta = context.theme.obtainStyledAttributes(R.styleable.ThemeStyleColors)
    return ta.getResourceId(R.styleable.ThemeStyleColors_themePrimary, R.color.colorPrimary)
}