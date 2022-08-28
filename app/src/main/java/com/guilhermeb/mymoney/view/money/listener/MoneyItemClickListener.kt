package com.guilhermeb.mymoney.view.money.listener

import android.os.Bundle
import android.view.View
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.guilhermeb.mymoney.R
import com.guilhermeb.mymoney.view.money.adapter.MoneyItemAdapter
import com.guilhermeb.mymoney.view.money.fragment.MoneyItemDetailFragment

class MoneyItemClickListener(
    private val itemId: Long = 0,
    private val isTablet: Boolean,
    private val newItem: Boolean,
    var editItem: Boolean,
    private val rootView: View,
    private val viewHolder: RecyclerView.ViewHolder? = null
) : View.OnClickListener {

    override fun onClick(view: View?) {
        val bundle = Bundle()
        bundle.putLong(MoneyItemDetailFragment.ARG_ITEM_ID, itemId)
        bundle.putBoolean(MoneyItemDetailFragment.ARG_IS_TABLET, isTablet)
        bundle.putBoolean(MoneyItemDetailFragment.ARG_NEW_ITEM, newItem)
        bundle.putBoolean(MoneyItemDetailFragment.ARG_EDIT_ITEM, editItem)
        // Leaving this not using view binding as it relies on if the view is visible the current
        // layout configuration (layout, layout-sw600dp)
        val itemDetailFragmentContainer: View? =
            rootView.findViewById(R.id.item_detail_nav_container)
        if (itemDetailFragmentContainer != null) {
            itemDetailFragmentContainer.findNavController()
                .navigate(R.id.fragment_item_detail, bundle)
        } else {
            rootView.findNavController().navigate(R.id.show_item_detail, bundle)
        }
        markOrUnmarkSelectedItem(rootView, viewHolder)
    }

    companion object {
        fun markOrUnmarkSelectedItem(rootView: View, viewHolder: RecyclerView.ViewHolder? = null) {
            val recyclerView = rootView.findViewById<RecyclerView>(R.id.recycler_view_money)
            recyclerView.adapter?.let {
                if (recyclerView.adapter is MoneyItemAdapter) {
                    val moneyItemAdapter: MoneyItemAdapter =
                        recyclerView.adapter as MoneyItemAdapter
                    var selectedPos = RecyclerView.NO_POSITION
                    viewHolder?.let {
                        selectedPos = viewHolder.bindingAdapterPosition
                    }
                    // Updating old as well as new positions
                    moneyItemAdapter.notifyItemChanged(moneyItemAdapter.selectedPosition)
                    moneyItemAdapter.selectedPosition = selectedPos
                    moneyItemAdapter.notifyItemChanged(moneyItemAdapter.selectedPosition)
                }
            }
        }
    }
}