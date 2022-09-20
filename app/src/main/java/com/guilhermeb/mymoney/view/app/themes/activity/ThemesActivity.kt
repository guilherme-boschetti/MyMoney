package com.guilhermeb.mymoney.view.app.themes.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.guilhermeb.mymoney.R
import com.guilhermeb.mymoney.common.constant.Constants
import com.guilhermeb.mymoney.common.util.Themes
import com.guilhermeb.mymoney.common.util.saveTheme
import com.guilhermeb.mymoney.databinding.ActivityThemesBinding
import com.guilhermeb.mymoney.view.app.activity.AbstractActivity

class ThemesActivity : AbstractActivity(), View.OnClickListener {

    private lateinit var themesViewBinding: ActivityThemesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        themesViewBinding = ActivityThemesBinding.inflate(layoutInflater)
        setContentView(themesViewBinding.root)
        setTitle(R.string.themes)

        handleClicks()
    }

    private fun handleClicks() {
        themesViewBinding.themeGreen.setOnClickListener(this)
        themesViewBinding.themeRed.setOnClickListener(this)
        themesViewBinding.themeBlue.setOnClickListener(this)
        themesViewBinding.themePink.setOnClickListener(this)
        themesViewBinding.themePurple.setOnClickListener(this)
    }

    private fun changeTheme(viewId: Int): Boolean {
        return when (viewId) {
            R.id.theme_green -> {
                saveTheme(Themes.GREEN)
                true
            }
            R.id.theme_red -> {
                saveTheme(Themes.RED)
                true
            }
            R.id.theme_blue -> {
                saveTheme(Themes.BLUE)
                true
            }
            R.id.theme_pink -> {
                saveTheme(Themes.PINK)
                true
            }
            R.id.theme_purple -> {
                saveTheme(Themes.PURPLE)
                true
            }
            else -> false
        }
    }

    override fun onClick(view: View?) {
        view?.let {
            val change = changeTheme(view.id)
            if (change) {
                Intent().apply {
                    putExtra(Constants.INTENT_EXTRA_KEY_THEME_CHANGED, true)
                    setResult(RESULT_OK, this)
                }
                recreate()
            }
        }
    }
}