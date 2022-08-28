package com.guilhermeb.mymoney.view.money.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.guilhermeb.mymoney.model.data.local.room.entity.money.Money
import com.guilhermeb.mymoney.view.money.adapter.viewholder.MoneyItemViewHolder
import com.guilhermeb.mymoney.view.money.listener.MoneyItemClickListener

class MoneyItemAdapter(
    private val deleteCallback: DeleteMoneyItemCallback,
    private val isTablet: Boolean,
    private val newItem: Boolean,
    private val rootView: View
) : ListAdapter<Money, RecyclerView.ViewHolder>(MoneyDiffCallBack()) {

    var selectedPosition = RecyclerView.NO_POSITION

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MoneyItemViewHolder.from(parent, this)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MoneyItemViewHolder) {
            val moneyItem = currentList[position]
            holder.bind(
                moneyItem,
                MoneyItemClickListener(moneyItem.id, isTablet, false, newItem, rootView, holder)
            )
            holder.itemView.isSelected = selectedPosition == position
        }
    }

    fun removeItemAt(position: Int) {
        val moneyItem = currentList[position]
        deleteCallback.deleteMoneyItem(moneyItem)
    }

    class MoneyDiffCallBack : DiffUtil.ItemCallback<Money>() {

        override fun areItemsTheSame(oldItem: Money, newItem: Money): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Money, newItem: Money): Boolean {
            return oldItem == newItem
        }
    }

    interface DeleteMoneyItemCallback {
        fun deleteMoneyItem(moneyItem: Money)
    }
}