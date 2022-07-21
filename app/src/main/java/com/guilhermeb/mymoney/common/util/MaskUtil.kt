package com.guilhermeb.mymoney.common.util

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import java.math.BigDecimal
import java.text.DecimalFormat
import kotlin.math.pow

class MaskUtil {
    companion object {
        /*const val FORMAT_CPF = "###.###.###-##"
        const val FORMAT_CNPJ = "##.###.###/####-##"
        const val FORMAT_PHONE = "(###)####-####"
        const val FORMAT_CEL = "(###)#####-####"
        const val FORMAT_CEP = "#####-###"
        const val FORMAT_HOURS_MINUTES = "##:##"
        const val FORMAT_HOURS_MINUTES_SECONDS = "##:##:##"
        const val FORMAT_DATE_SHORT_YEAR = "##/##/##"*/
        const val FORMAT_DATE = "##/##/####"

        private const val FORMAT_CURRENCY_BR = "#,##0.00;-#,##0.00"
        private const val FORMAT_CURRENCY_ES = "#,##0.00;-#,##0.00"
        private const val FORMAT_CURRENCY_US = "#,###.##;-#,###.##"

        private const val PT_BR = "pt-BR"
        private const val ES_ES = "es-ES"
        private const val EN_US = "en-US"

        private const val CURRENCY_SIGN_BR = "R\$"
        private const val CURRENCY_SIGN_ES = "€"
        private const val CURRENCY_SIGN_US = "US\$"

        fun getDecimalFormat(): DecimalFormat {
            return when (getCurrentLanguageLocale()) {
                PT_BR -> DecimalFormat(FORMAT_CURRENCY_BR)
                ES_ES -> DecimalFormat(FORMAT_CURRENCY_ES)
                EN_US -> DecimalFormat(FORMAT_CURRENCY_US)
                else -> DecimalFormat(FORMAT_CURRENCY_US)
            }
        }

        private fun getCurrencySign(): String {
            return when (getCurrentLanguageLocale()) {
                PT_BR -> CURRENCY_SIGN_BR
                ES_ES -> CURRENCY_SIGN_ES
                EN_US -> CURRENCY_SIGN_US
                else -> CURRENCY_SIGN_US
            }
        }

        fun parseValue(formattedValue: String): Number? {
            return getDecimalFormat().parse(formattedValue)
        }

        fun getFormattedValueText(value: BigDecimal): String {
            return getDecimalFormat().format(value)
        }

        fun getFormattedCurrencyValueText(value: BigDecimal): String {
            return getCurrencySign() + ": " + getFormattedValueText(value)
        }

        private fun unmask(text: String): String {
            return text.replace("[.]".toRegex(), "").replace("[:]".toRegex(), "")
                .replace("[-]".toRegex(), "").replace("[/]".toRegex(), "")
                .replace("[(]".toRegex(), "").replace("[)]".toRegex(), "")
                .replace("[ ]".toRegex(), "")
        }

        fun mask(editText: EditText, mask: String?): TextWatcher {
            return object : TextWatcher {
                var isUpdating = false
                var old = ""
                override fun afterTextChanged(s: Editable) {}
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    if (mask == null) {
                        val str = s.toString()
                        val mascara: String
                        if (isUpdating) {
                            isUpdating = false
                            return
                        }
                        // get only the numbers
                        var auxValue = str.replace("[^\\d]".toRegex(), "")
                        if (auxValue.isEmpty()) {
                            auxValue = "0"
                        }
                        //Convert to BigDecimal
                        var aux = try {
                            BigDecimal(auxValue)
                        } catch (e: Exception) {
                            BigDecimal.ZERO
                        }
                        aux = aux.divide(
                            BigDecimal.valueOf(10.0.pow(2.0)),
                            2,
                            BigDecimal.ROUND_HALF_EVEN
                        )
                        mascara = getDecimalFormat().format(aux)
                        isUpdating = true
                        editText.setText(mascara)
                        editText.setSelection(mascara.length)
                    } else {
                        val str = unmask(s.toString())
                        var mascara = ""
                        if (isUpdating) {
                            old = str
                            isUpdating = false
                            return
                        }
                        if (str.length > old.length) {
                            var i = 0
                            for (m in mask.toCharArray()) {
                                if (m != '#' && str.length > old.length) {
                                    mascara += m
                                    continue
                                }
                                mascara += try {
                                    str[i]
                                } catch (e: Exception) {
                                    break
                                }
                                i++
                            }
                        } else {
                            mascara = s.toString()
                        }
                        isUpdating = true
                        editText.setText(mascara)
                        editText.setSelection(mascara.length)
                    }
                }
            }
        }

        fun mask(text: String, mask: String?): String {
            return if (mask == null) {
                // get only the numbers
                val auxValue = text.replace("[^\\d]".toRegex(), "")
                //Convert to BigDecimal
                var aux = BigDecimal(auxValue)
                aux = aux.divide(BigDecimal.valueOf(10.0.pow(2.0)), 2, BigDecimal.ROUND_HALF_EVEN)
                val mascara: String = getDecimalFormat().format(aux)
                mascara
            } else {
                val str = unmask(text)
                var mascara = ""
                var i = 0
                for (m in mask.toCharArray()) {
                    if (m != '#') {
                        mascara += m
                        continue
                    }
                    mascara += try {
                        str[i]
                    } catch (e: Exception) {
                        break
                    }
                    i++
                }
                mascara
            }
        }
    }
}