package com.guilhermeb.mymoney.view.money.adapter.databinding

import android.widget.CheckBox
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.guilhermeb.mymoney.R
import com.guilhermeb.mymoney.common.enum.MoneyType
import com.guilhermeb.mymoney.common.util.MaskUtil
import com.guilhermeb.mymoney.model.data.local.room.entity.money.Money
import java.math.BigDecimal

@BindingAdapter("app:setTextMoneyValue")
fun setTextMoneyValue(textView: TextView, value: BigDecimal) {
    textView.text = MaskUtil.getFormattedValueText(value)
}

@BindingAdapter("app:setMoneyType")
fun TextView.setMoneyType(type: String) {
    val typeToShow = if (MoneyType.INCOME.name == type) {
        this.context.getString(R.string.income)
    } else if (MoneyType.EXPENSE.name == type) {
        this.context.getString(R.string.expense)
    } else {
        ""
    }
    this.text = typeToShow
}

@BindingAdapter("app:setTextColorByType")
fun TextView.setTextColorByType(type: String) {
    val itemValueColor = ContextCompat.getColor(
        this.context,
        if (MoneyType.INCOME.name == type) {
            R.color.app_green
        } else if (MoneyType.EXPENSE.name == type) {
            R.color.red
        } else {
            R.color.color_text
        }
    )
    this.setTextColor(itemValueColor)
}

@BindingAdapter("app:setTextItemNotPaidByType")
fun TextView.setTextItemNotPaidByType(type: String) {
    if (MoneyType.INCOME.name == type) {
        this.setText(R.string.not_received)
    } else {
        this.setText(R.string.not_paid)
    }
}

@BindingAdapter("app:setTextChkItemPaidByType")
fun CheckBox.setTextChkItemPaidByType(type: String) {
    if (MoneyType.INCOME.name == type) {
        this.setText(R.string.received)
    } else {
        this.setText(R.string.paid)
    }
}

@BindingAdapter("app:setTextDueDay")
fun TextView.setTextDueDay(moneyItem: Money?) {
    moneyItem?.let {
        if (it.dueDay != null && it.dueDay!! > 0) {
            val label = if (MoneyType.INCOME.name == it.type) {
                this.context.getString(R.string.reception_day)
            } else {
                this.context.getString(R.string.due_day)
            }
            this.text = label + ": " + it.dueDay.toString()
        } else {
            this.setText(R.string.due_day)
        }
    }
}