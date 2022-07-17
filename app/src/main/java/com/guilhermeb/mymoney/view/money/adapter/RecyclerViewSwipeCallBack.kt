package com.guilhermeb.mymoney.view.money.adapter

import android.annotation.SuppressLint
import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import android.view.MotionEvent
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.guilhermeb.mymoney.R
import com.guilhermeb.mymoney.common.extension.showConfirmationDialog
import kotlin.math.abs

class RecyclerViewSwipeCallBack(private val mAdapter: MoneyItemAdapter) :
    ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

    @SuppressLint("ClickableViewAccessibility")
    override fun onChildDraw(
        c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
        dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean
    ) {
        super.onChildDraw(c, recyclerView, viewHolder, dX / 4, dY, actionState, isCurrentlyActive)

        val itemView = viewHolder.itemView

        val icon =
            AppCompatResources.getDrawable(itemView.context, R.drawable.ic_baseline_delete_24)
        val colorIcon = ContextCompat.getColor(itemView.context, R.color.color_text)
        DrawableCompat.setTint(icon!!, colorIcon)
        val colorBackground = ContextCompat.getColor(itemView.context, R.color.red)
        val background = ColorDrawable(colorBackground)

        val iconMargin: Int = (((abs(dX) / 4) - icon.intrinsicWidth) / 2).toInt()
        val iconTop: Int = itemView.top + (itemView.height - icon.intrinsicHeight) / 2
        val iconBottom = iconTop + icon.intrinsicHeight

        when {
            dX > 0 -> { // Swiping to the right
                val iconRight: Int = itemView.left + iconMargin + icon.intrinsicWidth
                val iconLeft: Int = itemView.left + iconMargin
                icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                background.setBounds(
                    itemView.left,
                    itemView.top,
                    itemView.left + dX.toInt(),
                    itemView.bottom
                )

                recyclerView.setOnTouchListener { _, motionEvent ->
                    if (motionEvent.action == MotionEvent.ACTION_UP) {
                        if (motionEvent.rawX <= (itemView.right - itemView.left) / 4) {
                            itemView.context.showConfirmationDialog(
                                R.string.are_you_sure_you_want_to_delete
                            ) {
                                mAdapter.removeItemAt(viewHolder.bindingAdapterPosition)
                            }
                        }
                    }
                    false
                }
            }
            dX < 0 -> { // Swiping to the left
                val iconLeft: Int = itemView.right - iconMargin - icon.intrinsicWidth
                val iconRight: Int = itemView.right - iconMargin
                icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                background.setBounds(
                    itemView.right + dX.toInt(),
                    itemView.top,
                    itemView.right,
                    itemView.bottom
                )

                recyclerView.setOnTouchListener { _, motionEvent ->
                    if (motionEvent.action == MotionEvent.ACTION_UP) {
                        if (motionEvent.rawX >= ((itemView.right - itemView.left) / 4) * 3) {
                            itemView.context.showConfirmationDialog(
                                R.string.are_you_sure_you_want_to_delete
                            ) {
                                mAdapter.removeItemAt(viewHolder.bindingAdapterPosition)
                            }
                        }
                    }
                    false
                }
            }
            else -> { // view is unSwiped
                background.setBounds(0, 0, 0, 0)

                recyclerView.setOnTouchListener(null)
            }
        }

        background.draw(c)
        icon.draw(c)
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        //mAdapter.removeItemAt(viewHolder.bindingAdapterPosition)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }
}