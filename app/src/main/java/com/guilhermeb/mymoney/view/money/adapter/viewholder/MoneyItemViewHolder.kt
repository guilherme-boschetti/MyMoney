package com.guilhermeb.mymoney.view.money.adapter.viewholder

import android.content.Context
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ImageSpan
import android.view.ContextMenu
import android.view.ContextMenu.ContextMenuInfo
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.guilhermeb.mymoney.MyMoneyApplication
import com.guilhermeb.mymoney.R
import com.guilhermeb.mymoney.common.enum.MoneyType
import com.guilhermeb.mymoney.common.extension.getAndroidTextColorPrimary
import com.guilhermeb.mymoney.common.extension.showConfirmationDialog
import com.guilhermeb.mymoney.common.util.MaskUtil
import com.guilhermeb.mymoney.databinding.ItemMoneyBinding
import com.guilhermeb.mymoney.model.data.local.room.entity.money.Money
import com.guilhermeb.mymoney.view.money.adapter.MoneyItemAdapter
import com.guilhermeb.mymoney.view.money.listener.MoneyItemClickListener

class MoneyItemViewHolder(
    private val itemMoneyViewBinding: ItemMoneyBinding,
    private val mAdapter: MoneyItemAdapter
) :
    RecyclerView.ViewHolder(itemMoneyViewBinding.root), View.OnCreateContextMenuListener {

    private lateinit var clickListener: MoneyItemClickListener

    fun bind(moneyItem: Money, clickListener: MoneyItemClickListener) {
        this.clickListener = clickListener

        val context = MyMoneyApplication.getInstance().applicationContext

        itemMoneyViewBinding.txtItemTitle.text = moneyItem.title
        itemMoneyViewBinding.txtItemDescription.text = moneyItem.description
        itemMoneyViewBinding.txtItemValue.text = MaskUtil.getFormattedValueText(moneyItem.value)

        if (moneyItem.dueDay != null && moneyItem.dueDay!! > 0) {
            val label = if (MoneyType.INCOME.name == moneyItem.type) {
                context.getString(R.string.reception_day)
            } else {
                context.getString(R.string.due_day)
            }
            itemMoneyViewBinding.txtItemDueDay.visibility = View.VISIBLE
            itemMoneyViewBinding.txtItemDueDay.text = label + ": " + moneyItem.dueDay.toString()
        } else {
            itemMoneyViewBinding.txtItemDueDay.visibility = View.INVISIBLE
        }

        if (MoneyType.INCOME.name == moneyItem.type) {
            itemMoneyViewBinding.txtItemNotPaid.text =
                context.getString(R.string.not_received)
            itemMoneyViewBinding.chkItemPaid.text =
                context.getString(R.string.received)
        } else {
            itemMoneyViewBinding.txtItemNotPaid.text =
                context.getString(R.string.not_paid)
            itemMoneyViewBinding.chkItemPaid.text =
                context.getString(R.string.paid)
        }
        if (moneyItem.paid) {
            itemMoneyViewBinding.txtItemNotPaid.visibility = View.INVISIBLE
            itemMoneyViewBinding.chkItemPaid.visibility = View.VISIBLE
            itemMoneyViewBinding.chkItemPaid.isChecked = true
        } else {
            itemMoneyViewBinding.txtItemNotPaid.visibility = View.VISIBLE
            itemMoneyViewBinding.chkItemPaid.visibility = View.INVISIBLE
            itemMoneyViewBinding.chkItemPaid.isChecked = false
        }

        val type = if (MoneyType.INCOME.name == moneyItem.type) {
            context.getString(R.string.income)
        } else if (MoneyType.EXPENSE.name == moneyItem.type) {
            context.getString(R.string.expense)
        } else {
            ""
        }
        itemMoneyViewBinding.txtItemType.text = type

        val itemValueColor = ContextCompat.getColor(
            context,
            if (MoneyType.INCOME.name == moneyItem.type) {
                R.color.app_green
            } else if (MoneyType.EXPENSE.name == moneyItem.type) {
                R.color.red
            } else {
                R.color.color_text
            }
        )
        itemMoneyViewBinding.txtItemValue.setTextColor(itemValueColor)

        itemMoneyViewBinding.root.setOnClickListener(clickListener)

        itemMoneyViewBinding.root.setOnCreateContextMenuListener(this)
    }

    companion object {
        fun from(parent: ViewGroup, adapter: MoneyItemAdapter): MoneyItemViewHolder {
            val itemMoneyViewBinding: ItemMoneyBinding =
                ItemMoneyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return MoneyItemViewHolder(itemMoneyViewBinding, adapter)
        }
    }

    private fun menuWithIconAndText(
        context: Context,
        icon: Drawable?,
        title: String
    ): CharSequence {
        icon?.let {
            // Change menu icon color programmatically
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                icon.colorFilter =
                    BlendModeColorFilter(context.getAndroidTextColorPrimary(), BlendMode.SRC_IN)
            } else {
                @Suppress("DEPRECATION")
                icon.setColorFilter(
                    context.getAndroidTextColorPrimary(), PorterDuff.Mode.SRC_IN
                )
            }

            icon.setBounds(0, 0, icon.intrinsicWidth, icon.intrinsicHeight)
            val spannableString = SpannableString("    $title")
            val imageSpan = ImageSpan(icon, ImageSpan.ALIGN_BOTTOM)
            spannableString.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            return spannableString
        }
        return title
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenuInfo?) {
        val context = MyMoneyApplication.getInstance().applicationContext
        // groupId, itemId, order, title
        val menuItemView = menu.add(
            1,
            1,
            1,
            menuWithIconAndText(
                context,
                AppCompatResources.getDrawable(context, R.drawable.ic_baseline_visibility_24),
                context.getString(R.string.view)
            )
        )
        val menuItemEdit = menu.add(
            1,
            2,
            2,
            menuWithIconAndText(
                context,
                AppCompatResources.getDrawable(context, R.drawable.ic_baseline_edit_24),
                context.getString(R.string.edit)
            )
        )
        val menuItemDelete = menu.add(
            1,
            3,
            3,
            menuWithIconAndText(
                context,
                AppCompatResources.getDrawable(context, R.drawable.ic_baseline_delete_24),
                context.getString(R.string.delete)
            )
        )

        menuItemView.setOnMenuItemClickListener {
            clickListener.editItem = false
            clickListener.onClick(itemMoneyViewBinding.root)
            return@setOnMenuItemClickListener true
        }
        menuItemEdit.setOnMenuItemClickListener {
            clickListener.editItem = true
            clickListener.onClick(itemMoneyViewBinding.root)
            return@setOnMenuItemClickListener true
        }
        menuItemDelete.setOnMenuItemClickListener {
            itemView.context.showConfirmationDialog(
                R.string.are_you_sure_you_want_to_delete
            ) {
                mAdapter.removeItemAt(bindingAdapterPosition)
            }
            return@setOnMenuItemClickListener true
        }
    }
}