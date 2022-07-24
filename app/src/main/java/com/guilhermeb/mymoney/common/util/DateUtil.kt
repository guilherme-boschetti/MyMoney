package com.guilhermeb.mymoney.common.util

import android.annotation.SuppressLint
import com.guilhermeb.mymoney.MyMoneyApplication
import com.guilhermeb.mymoney.R
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("ConstantLocale")
class DateUtil {
    companion object {
        val YEAR_MONTH_DAY_HOURS_MINUTES_SECONDS =
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss", getLocaleOrDefault())
        val DAY_MONTH_YEAR =
            SimpleDateFormat("dd/MM/yyyy", getLocaleOrDefault())

        init {
            val timeZone = TimeZone.getTimeZone("UTC")
            //YEAR_MONTH_DAY_HOURS_MINUTES_SECONDS.timeZone = timeZone
            DAY_MONTH_YEAR.timeZone = timeZone
        }

        fun getMonthNameByMonthNumber(month: Int): String {
            val context = MyMoneyApplication.getInstance()
            return when (month) {
                1 -> context.getString(R.string.january)
                2 -> context.getString(R.string.february)
                3 -> context.getString(R.string.march)
                4 -> context.getString(R.string.april)
                5 -> context.getString(R.string.may)
                6 -> context.getString(R.string.june)
                7 -> context.getString(R.string.july)
                8 -> context.getString(R.string.august)
                9 -> context.getString(R.string.september)
                10 -> context.getString(R.string.october)
                11 -> context.getString(R.string.november)
                12 -> context.getString(R.string.december)
                else -> ""
            }
        }

        fun getMonthNumberByMonthName(monthName: String): Int {
            val context = MyMoneyApplication.getInstance()
            return when (monthName) {
                context.getString(R.string.january) -> 1
                context.getString(R.string.february) -> 2
                context.getString(R.string.march) -> 3
                context.getString(R.string.april) -> 4
                context.getString(R.string.may) -> 5
                context.getString(R.string.june) -> 6
                context.getString(R.string.july) -> 7
                context.getString(R.string.august) -> 8
                context.getString(R.string.september) -> 9
                context.getString(R.string.october) -> 10
                context.getString(R.string.november) -> 11
                context.getString(R.string.december) -> 12
                else -> 0
            }
        }
    }
}