package com.guilhermeb.mymoney.view.app.settings.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import com.guilhermeb.mymoney.BuildConfig
import com.guilhermeb.mymoney.R
import com.guilhermeb.mymoney.common.constant.Constants
import com.guilhermeb.mymoney.common.helper.SharedPreferencesHelper
import com.guilhermeb.mymoney.common.extension.isUiModeNightActive
import com.guilhermeb.mymoney.common.helper.getSharedPreferencesKey
import com.guilhermeb.mymoney.common.util.getCurrentLanguageLocale
import com.guilhermeb.mymoney.common.util.setLocale
import com.guilhermeb.mymoney.databinding.ActivitySettingsBinding
import com.guilhermeb.mymoney.databinding.DialogNightModeBinding
import com.guilhermeb.mymoney.view.app.activity.AbstractActivity
import com.guilhermeb.mymoney.view.app.themes.activity.ThemesActivity
import kotlin.collections.ArrayList

class SettingsActivity : AbstractActivity() {

    private lateinit var settingsViewBinding: ActivitySettingsBinding
    private val sharedPreferencesHelper = SharedPreferencesHelper.getInstance()

    private var themesResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data: Intent? = result.data
                data?.let {
                    if ((it.hasExtra(Constants.INTENT_EXTRA_KEY_THEME_CHANGED) && it.getBooleanExtra(
                            Constants.INTENT_EXTRA_KEY_THEME_CHANGED,
                            false
                        ))
                    ) {
                        Intent().apply {
                            putExtra(Constants.INTENT_EXTRA_KEY_THEME_CHANGED, true)
                            setResult(RESULT_OK, this)
                        }
                        recreate()
                    }
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        settingsViewBinding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(settingsViewBinding.root)
        setTitle(R.string.settings)
        initScreen()
        addListeners()
    }

    private fun setupFeaturesFlavors() {
        if (BuildConfig.IS_FREE || BuildConfig.IS_PRO) {
            settingsViewBinding.swhUsePreviousMonthBalance.visibility = View.GONE
        }
        if (BuildConfig.IS_FREE) {
            settingsViewBinding.swhUpdateChangesInRealTime.visibility = View.GONE
            settingsViewBinding.btnThemes.visibility = View.GONE
        }
    }

    private fun initScreen() {
        setupFeaturesFlavors()

        settingsViewBinding.apply {

            // == -- AutoCompleteLanguage == --
            autocompleteLanguage.setText(getCurrentLanguage())
            val adapterLanguage: ArrayAdapter<String> = ArrayAdapter<String>(
                this@SettingsActivity,
                android.R.layout.simple_spinner_dropdown_item,
                getLanguageList()
            )
            autocompleteLanguage.setAdapter(adapterLanguage)
            // == -- == --

            // == -- AutoCompleteCurrency == --
            autocompleteCurrency.setText(getCurrentCurrency())
            val adapterCurrency: ArrayAdapter<String> = ArrayAdapter<String>(
                this@SettingsActivity,
                android.R.layout.simple_spinner_dropdown_item,
                getCurrencyList()
            )
            autocompleteCurrency.setAdapter(adapterCurrency)
            // == -- == --

            // == -- SwitchNightMode == --
            swhNightMode.isChecked = isUiModeNightActive()
            // == -- == --

            // == -- SwitchUsePreviousMonthBalance == --
            swhUsePreviousMonthBalance.isChecked = sharedPreferencesHelper.getValue(
                getSharedPreferencesKey(Constants.PREVIOUS_MONTH_BALANCE),
                false
            )
            // == -- == --

            // == -- SwhUpdateChangesInRealTime == --
            swhUpdateChangesInRealTime.isChecked = sharedPreferencesHelper.getValue(
                getSharedPreferencesKey(Constants.UPDATE_CHANGES_IN_REAL_TIME),
                false
            )
            // == -- == --

            // == -- BtnThemes == --
            btnThemes.isEnabled = !isUiModeNightActive()
            // == -- == --
        }
    }

    private fun addListeners() {
        settingsViewBinding.apply {

            // == -- AutoCompleteLanguage == --
            autocompleteLanguage.onItemClickListener =
                AdapterView.OnItemClickListener { _, _, position, _ ->
                    val languageCodesArray = resources.getStringArray(R.array.language_codes)
                    sharedPreferencesHelper.setValue(
                        getSharedPreferencesKey(Constants.LOCALE),
                        languageCodesArray[position]
                    )
                    setLocale(applicationContext)
                    Intent().apply {
                        putExtra(Constants.INTENT_EXTRA_KEY_LANGUAGE_CHANGED, true)
                        setResult(RESULT_OK, this)
                    }
                    recreate()
                }
            // == -- == --

            // == -- AutoCompleteCurrency == --
            autocompleteCurrency.onItemClickListener =
                AdapterView.OnItemClickListener { _, _, position, _ ->
                    val currenciesArray = resources.getStringArray(R.array.currencies)
                    sharedPreferencesHelper.setValue(
                        getSharedPreferencesKey(Constants.CURRENCY),
                        currenciesArray[position]
                    )
                }
            // == -- == --

            // == -- SwitchNightMode == --
            swhNightMode.setOnLongClickListener {
                NightModeDialog(this@SettingsActivity).openDialog()
                return@setOnLongClickListener true
            }
            swhNightMode.setOnCheckedChangeListener { _, checked ->
                if (checked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
                sharedPreferencesHelper.setValue(
                    getSharedPreferencesKey(Constants.NIGHT_MODE),
                    if (checked) Constants.YES else Constants.NO
                )
                btnThemes.isEnabled = !checked
            }
            // == -- == --

            // == -- SwitchUsePreviousMonthBalance == --
            swhUsePreviousMonthBalance.setOnCheckedChangeListener { _, checked ->
                sharedPreferencesHelper.setValue(
                    getSharedPreferencesKey(Constants.PREVIOUS_MONTH_BALANCE),
                    checked
                )
                Intent().apply {
                    putExtra(Constants.INTENT_EXTRA_KEY_PREVIOUS_MONTH_BALANCE_CHANGED, true)
                    setResult(RESULT_OK, this)
                }
            }
            // == -- == --

            // == -- SwhUpdateChangesInRealTime == --
            swhUpdateChangesInRealTime.setOnCheckedChangeListener { _, checked ->
                sharedPreferencesHelper.setValue(
                    getSharedPreferencesKey(Constants.UPDATE_CHANGES_IN_REAL_TIME),
                    checked
                )
                Intent().apply {
                    putExtra(Constants.INTENT_EXTRA_KEY_UPDATE_CHANGES_IN_REAL_TIME, true)
                    setResult(RESULT_OK, this)
                }
            }
            // == -- == --

            // == -- BtnThemes == --
            btnThemes.setOnClickListener {
                val intent = Intent(this@SettingsActivity, ThemesActivity::class.java)
                themesResultLauncher.launch(intent)
            }
            // == -- == --
        }
    }

    private fun getLanguageList(): ArrayList<String> {
        val languages = ArrayList<String>()
        val languagesArray = resources.getStringArray(R.array.languages)
        for (language in languagesArray) {
            languages.add(language)
        }
        return languages
    }

    private fun getCurrencyList(): ArrayList<String> {
        val currencies = ArrayList<String>()
        val currenciesArray = resources.getStringArray(R.array.currencies)
        for (currency in currenciesArray) {
            currencies.add(currency)
        }
        return currencies
    }

    private fun getCurrentLanguageSelectionPosition(): Int {
        val languageCodesArray = resources.getStringArray(R.array.language_codes)
        val localeString: String? = getCurrentLanguageLocale()
        val selectionPosition: Int = if (localeString != null) {
            when (localeString) {
                languageCodesArray[0] -> 0
                languageCodesArray[1] -> 1
                languageCodesArray[2] -> 2
                else -> 0
            }
        } else {
            0
        }
        sharedPreferencesHelper.setValue(
            getSharedPreferencesKey(Constants.LOCALE),
            languageCodesArray[selectionPosition]
        )
        return selectionPosition
    }

    private fun getCurrentCurrencySelectionPosition(): Int {
        val currenciesArray = resources.getStringArray(R.array.currencies)
        val sharedPreferencesKeyCurrency = getSharedPreferencesKey(Constants.CURRENCY)
        val currency: String? = sharedPreferencesHelper.getValue(sharedPreferencesKeyCurrency, null)
        if (currency != null) {
            when (currency) {
                currenciesArray[0] -> {
                    return 0
                }
                currenciesArray[1] -> {
                    return 1
                }
                currenciesArray[2] -> {
                    return 2
                }
            }
        }
        val selectionPosition = getCurrentLanguageSelectionPosition()
        sharedPreferencesHelper.setValue(
            sharedPreferencesKeyCurrency,
            currenciesArray[selectionPosition]
        )
        return selectionPosition
    }

    private fun getCurrentLanguage(): String {
        val languagesArray = resources.getStringArray(R.array.languages)
        return languagesArray[getCurrentLanguageSelectionPosition()]
    }

    private fun getCurrentCurrency(): String {
        val currenciesArray = resources.getStringArray(R.array.currencies)
        return currenciesArray[getCurrentCurrencySelectionPosition()]
    }

    class NightModeDialog(val context: Context) {

        private lateinit var nightModeViewBinding: DialogNightModeBinding
        private lateinit var dialog: AlertDialog
        private val sharedPreferencesHelper = SharedPreferencesHelper.getInstance()

        fun openDialog() {
            val builder: AlertDialog.Builder = AlertDialog.Builder(context)
            nightModeViewBinding = DialogNightModeBinding.inflate(LayoutInflater.from(context))
            init()
            handleClicks()
            builder.setView(nightModeViewBinding.root)
            dialog = builder.create()
            dialog.show()
        }

        private fun init() {
            when (sharedPreferencesHelper.getValue(
                getSharedPreferencesKey(Constants.NIGHT_MODE),
                Constants.FOLLOW_SYSTEM
            )) {
                Constants.NO -> nightModeViewBinding.rBtnModeNightNo.isChecked = true
                Constants.YES -> nightModeViewBinding.rBtnModeNightYes.isChecked = true
                Constants.FOLLOW_SYSTEM -> nightModeViewBinding.rBtnModeNightSystemDefault.isChecked =
                    true
            }
        }

        private fun handleClicks() {
            nightModeViewBinding.imgBtnClose.setOnClickListener {
                dialog.dismiss()
            }
            nightModeViewBinding.rGrpModeNight.setOnCheckedChangeListener { _, _ ->
                val sharedPreferencesKey = getSharedPreferencesKey(Constants.NIGHT_MODE)
                if (nightModeViewBinding.rBtnModeNightNo.isChecked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    sharedPreferencesHelper.setValue(sharedPreferencesKey, Constants.NO)
                } else if (nightModeViewBinding.rBtnModeNightYes.isChecked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    sharedPreferencesHelper.setValue(sharedPreferencesKey, Constants.YES)
                } else if (nightModeViewBinding.rBtnModeNightSystemDefault.isChecked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                    sharedPreferencesHelper.setValue(sharedPreferencesKey, Constants.FOLLOW_SYSTEM)
                }
                dialog.dismiss()
            }
        }
    }
}