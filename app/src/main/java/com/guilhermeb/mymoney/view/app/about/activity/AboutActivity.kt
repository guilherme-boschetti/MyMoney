package com.guilhermeb.mymoney.view.app.about.activity

import android.os.Bundle
import android.widget.TextView
import com.guilhermeb.mymoney.R
import com.guilhermeb.mymoney.common.util.getAppVersion
import com.guilhermeb.mymoney.view.app.activity.AbstractActivity

class AboutActivity : AbstractActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        setTitle(R.string.about)

        showAppVersion()
    }

    private fun showAppVersion() {
        val txtAppVersion = findViewById<TextView>(R.id.txt_app_version)
        txtAppVersion.text = getAppVersion()
    }
}