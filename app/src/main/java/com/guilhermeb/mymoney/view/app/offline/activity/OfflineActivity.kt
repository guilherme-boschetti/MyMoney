package com.guilhermeb.mymoney.view.app.offline.activity

import android.os.Bundle
import com.guilhermeb.mymoney.R
import com.guilhermeb.mymoney.view.app.activity.AbstractActivity

class OfflineActivity : AbstractActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_offline)
        setTitle(R.string.app_name)
    }
}