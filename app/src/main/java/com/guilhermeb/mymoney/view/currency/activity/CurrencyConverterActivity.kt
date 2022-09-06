package com.guilhermeb.mymoney.view.currency.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.guilhermeb.mymoney.R
import com.guilhermeb.mymoney.common.extension.afterTextChanged
import com.guilhermeb.mymoney.common.extension.changeHintOnFocusChange
import com.guilhermeb.mymoney.common.util.MaskUtil
import com.guilhermeb.mymoney.common.util.isNetworkAvailable
import com.guilhermeb.mymoney.databinding.ActivityCurrencyConverterBinding
import com.guilhermeb.mymoney.view.app.activity.AbstractActivity
import com.guilhermeb.mymoney.view.app.offline.activity.OfflineActivity
import com.guilhermeb.mymoney.viewmodel.currency.CurrencyConverterViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.math.BigDecimal
import javax.inject.Inject

@AndroidEntryPoint
class CurrencyConverterActivity : AbstractActivity() {

    private lateinit var currencyConverterViewBinding: ActivityCurrencyConverterBinding

    @Inject
    lateinit var currencyViewModel: CurrencyConverterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currencyConverterViewBinding = ActivityCurrencyConverterBinding.inflate(layoutInflater)
        setContentView(currencyConverterViewBinding.root)
        setTitle(R.string.currency_converter)

        initScreen()
        observeProperties()
    }

    override fun onDestroy() {
        super.onDestroy()
        currencyViewModel.clearData()
    }

    private fun initScreen() {
        loadAutoCompleteTexts()
        handleInputEvents()
        addListeners()
        handleClicks()
    }

    private fun observeProperties() {
        currencyViewModel.currencyApiResponse.observe(this) { response ->
            val amountString = currencyConverterViewBinding.edtAmount.text.toString()
            val amount = if (amountString.isNotEmpty()) {
                BigDecimal(MaskUtil.parseValue(amountString).toString())
            } else {
                BigDecimal.ZERO
            }

            if (response.data == null && response.errorMessage != null && response.errorMessage.isEmpty()) {

                currencyConverterViewBinding.txtResult.text = getString(R.string.failure)
                changeLoadingVisibility(false)

            } else {

                var result = BigDecimal.ZERO
                response.data?.let { data ->
                    data[0]?.let {
                        result = amount.multiply(BigDecimal(it.getBid().toString()))
                    }
                }

                val from = currencyConverterViewBinding.autocompleteFromCurrency.text.toString()
                val to = currencyConverterViewBinding.autocompleteToCurrency.text.toString()
                val resultText =
                    "$amountString $from = ${MaskUtil.getFormattedValueText(result)} $to"

                currencyConverterViewBinding.txtResult.text = response.errorMessage ?: resultText

                changeLoadingVisibility(false)
            }

            currencyConverterViewBinding.btnCancel.setText(R.string.back)
        }

        currencyViewModel.currencyConverterFormState.observe(this) { formState ->
            // disable convert button unless form is completed
            currencyConverterViewBinding.btnConvert.isEnabled = formState.isFormCompleted

            if (formState.amountError != null) {
                currencyConverterViewBinding.inAmount.error = getString(formState.amountError)
            } else {
                currencyConverterViewBinding.inAmount.error
            }
            if (formState.fromCurrencyError != null) {
                currencyConverterViewBinding.inFromCurrency.error =
                    getString(formState.fromCurrencyError)
            } else {
                currencyConverterViewBinding.inFromCurrency.error
            }
            if (formState.toCurrencyError != null) {
                currencyConverterViewBinding.inToCurrency.error =
                    getString(formState.toCurrencyError)
            } else {
                currencyConverterViewBinding.inToCurrency.error
            }

            currencyConverterViewBinding.txtResult.text = ""
            currencyConverterViewBinding.btnCancel.setText(R.string.cancel)
        }
    }

    private fun loadAutoCompleteTexts() {
        currencyConverterViewBinding.apply {
            val adapterLanguage: ArrayAdapter<String> = ArrayAdapter<String>(
                this@CurrencyConverterActivity,
                android.R.layout.simple_spinner_dropdown_item,
                resources.getStringArray(R.array.currencies_codes)
            )
            autocompleteFromCurrency.setAdapter(adapterLanguage)

            val adapterCurrency: ArrayAdapter<String> = ArrayAdapter<String>(
                this@CurrencyConverterActivity,
                android.R.layout.simple_spinner_dropdown_item,
                resources.getStringArray(R.array.currencies_codes)
            )
            autocompleteToCurrency.setAdapter(adapterCurrency)
        }
    }

    private fun handleInputEvents() {
        currencyConverterViewBinding.btnCancel.setOnClickListener {
            finish()
        }

        currencyConverterViewBinding.edtAmount.apply {

            changeHintOnFocusChange(
                this@CurrencyConverterActivity,
                getString(R.string.hint_value),
                ""
            )

            afterTextChanged {
                currencyConverterViewBinding.inAmount.error = null
                currencyViewModel.currencyConverterFormDataChanged(
                    currencyConverterViewBinding.edtAmount.text.toString(),
                    currencyConverterViewBinding.autocompleteFromCurrency.text.toString(),
                    currencyConverterViewBinding.autocompleteToCurrency.text.toString()
                )
            }

            addTextChangedListener(MaskUtil.mask(this, null))
        }
    }

    private fun addListeners() {
        currencyConverterViewBinding.apply {

            autocompleteFromCurrency.onItemClickListener =
                AdapterView.OnItemClickListener { _, _, _, _ ->
                    inFromCurrency.error = null
                    inToCurrency.error = null
                    currencyViewModel.currencyConverterFormDataChanged(
                        currencyConverterViewBinding.edtAmount.text.toString(),
                        currencyConverterViewBinding.autocompleteFromCurrency.text.toString(),
                        currencyConverterViewBinding.autocompleteToCurrency.text.toString()
                    )
                }

            autocompleteToCurrency.onItemClickListener =
                AdapterView.OnItemClickListener { _, _, _, _ ->
                    inFromCurrency.error = null
                    inToCurrency.error = null
                    currencyViewModel.currencyConverterFormDataChanged(
                        currencyConverterViewBinding.edtAmount.text.toString(),
                        currencyConverterViewBinding.autocompleteFromCurrency.text.toString(),
                        currencyConverterViewBinding.autocompleteToCurrency.text.toString()
                    )
                }
        }
    }

    private fun handleClicks() {
        currencyConverterViewBinding.btnConvert.setOnClickListener {
            if (isNetworkAvailable()) {
                if (currencyViewModel.isCurrencyConverterFormDataValid(
                        currencyConverterViewBinding.edtAmount.text.toString(),
                        currencyConverterViewBinding.autocompleteFromCurrency.text.toString(),
                        currencyConverterViewBinding.autocompleteToCurrency.text.toString()
                    )
                ) {
                    callApi()
                }
            } else {
                val intent = Intent(this@CurrencyConverterActivity, OfflineActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun callApi() {
        val from = currencyConverterViewBinding.autocompleteFromCurrency.text.toString()
        val to = currencyConverterViewBinding.autocompleteToCurrency.text.toString()
        val fromCurrencyTo = "$from-$to" // Example: "USD-BRL"
        changeLoadingVisibility(true)
        currencyViewModel.getCurrency(fromCurrencyTo)
    }

    private fun changeLoadingVisibility(visible: Boolean) {
        val visibility = if (visible) {
            View.VISIBLE
        } else {
            View.INVISIBLE
        }
        currencyConverterViewBinding.layoutProgressLoading.root.visibility = visibility

        // disable convert button while is loading
        currencyConverterViewBinding.btnConvert.isEnabled = !visible
    }
}