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
import androidx.recyclerview.widget.RecyclerView
import com.guilhermeb.mymoney.MyMoneyApplication
import com.guilhermeb.mymoney.R
import com.guilhermeb.mymoney.common.extension.getAndroidTextColorPrimary
import com.guilhermeb.mymoney.common.extension.showConfirmationDialog
import com.guilhermeb.mymoney.databinding.ItemMoneyBinding
import com.guilhermeb.mymoney.model.data.local.room.entity.money.Money
import com.guilhermeb.mymoney.view.money.adapter.MoneyItemAdapter
import com.guilhermeb.mymoney.view.money.listener.MoneyItemClickListener

class MoneyItemViewHolder(
    private val itemMoneyBinding: ItemMoneyBinding,
    private val mAdapter: MoneyItemAdapter
) :
    RecyclerView.ViewHolder(itemMoneyBinding.root), View.OnCreateContextMenuListener {

    private lateinit var clickListener: MoneyItemClickListener

    fun bind(moneyItem: Money, clickListener: MoneyItemClickListener) {
        this.clickListener = clickListener

        itemMoneyBinding.money = moneyItem

        itemMoneyBinding.root.setOnClickListener(clickListener)

        itemMoneyBinding.root.setOnCreateContextMenuListener(this)
    }

    companion object {
        fun from(parent: ViewGroup, adapter: MoneyItemAdapter): MoneyItemViewHolder {
            val itemMoneyBinding =
                ItemMoneyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return MoneyItemViewHolder(itemMoneyBinding, adapter)
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
            clickListener.onClick(itemMoneyBinding.root)
            return@setOnMenuItemClickListener true
        }
        menuItemEdit.setOnMenuItemClickListener {
            clickListener.editItem = true
            clickListener.onClick(itemMoneyBinding.root)
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