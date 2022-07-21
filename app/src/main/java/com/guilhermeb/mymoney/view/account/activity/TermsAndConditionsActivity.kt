package com.guilhermeb.mymoney.view.account.activity

import android.os.Bundle
import com.guilhermeb.mymoney.R
import com.guilhermeb.mymoney.view.app.activity.AbstractActivity

class TermsAndConditionsActivity : AbstractActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_terms_and_conditions)
        setTitle(R.string.terms_and_conditions_title)
    }
}