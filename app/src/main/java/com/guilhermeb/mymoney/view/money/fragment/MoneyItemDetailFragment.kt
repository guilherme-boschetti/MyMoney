package com.guilhermeb.mymoney.view.money.fragment

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.*
import android.view.animation.OvershootInterpolator
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.Nullable
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.guilhermeb.mymoney.R
import com.guilhermeb.mymoney.common.enum.MoneyType
import com.guilhermeb.mymoney.common.extension.afterTextChanged
import com.guilhermeb.mymoney.common.extension.changeHintOnFocusChange
import com.guilhermeb.mymoney.common.extension.hideView
import com.guilhermeb.mymoney.common.extension.showView
import com.guilhermeb.mymoney.common.util.DateUtil
import com.guilhermeb.mymoney.common.util.MaskUtil
import com.guilhermeb.mymoney.common.util.showToast
import com.guilhermeb.mymoney.databinding.FragmentMoneyItemDetailBinding
import com.guilhermeb.mymoney.model.data.local.room.entity.money.Money
import com.guilhermeb.mymoney.view.money.activity.MoneyHostActivity
import com.guilhermeb.mymoney.viewmodel.money.MoneyViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.math.BigDecimal
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class MoneyItemDetailFragment : Fragment() {

    private var itemId: Long = 0
    private var isTablet: Boolean = false
    private var newItem: Boolean = false
    private var editItem: Boolean = false

    private var update: Boolean = false

    private var _binding: FragmentMoneyItemDetailBinding? = null

    private val binding get() = _binding!! // This property is only valid between onCreateView and onDestroyView.

    @Inject
    lateinit var moneyViewModel: MoneyViewModel

    private var money: Money? = null

    private lateinit var adapterSubtypes: ArrayAdapter<String>

    /**
     * The fragment arguments.
     */
    companion object {
        const val ARG_ITEM_ID = "item_id"
        const val ARG_IS_TABLET = "is_tablet"
        const val ARG_NEW_ITEM = "new_item"
        const val ARG_EDIT_ITEM = "edit_item"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            if (it.containsKey(ARG_ITEM_ID)) {
                itemId = it.getLong(ARG_ITEM_ID)
            }
            if (it.containsKey(ARG_IS_TABLET)) {
                isTablet = it.getBoolean(ARG_IS_TABLET, true)
            }
            if (it.containsKey(ARG_NEW_ITEM)) {
                newItem = it.getBoolean(ARG_NEW_ITEM, false)
            }
            if (it.containsKey(ARG_EDIT_ITEM)) {
                editItem = it.getBoolean(ARG_EDIT_ITEM, false)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentMoneyItemDetailBinding.inflate(inflater, container, false)
        val rootView = binding.root

        if (activity is MoneyHostActivity && !isTablet) {
            (activity as MoneyHostActivity).showBackButton(true)
        }

        observeProperties()

        fetchData()

        initScreen()

        return rootView
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        clearMoneyItem()
    }

    private fun clearMoneyItem() {
        money = null
        moneyViewModel.clearMoneyItem()
    }

    private fun observeProperties() {
        moneyViewModel.moneyItem.observe(viewLifecycleOwner) {
            money = it
            if (money != null) {
                binding.apply {
                    it?.let {
                        edtTitle.setText(it.title)
                        edtDescription.setText(it.description)
                        edtValue.setText(
                            MaskUtil.mask(
                                it.value.toString(),
                                null
                            )
                        )
                        it.payDate?.let { payDate ->
                            edtPayDate.setText(DateUtil.DAY_MONTH_YEAR.format(payDate))
                        }
                        it.dueDay?.let { dueDay ->
                            edtDueDay.setText(dueDay.toString())
                        }
                        chkPaid.isChecked = it.paid
                        chkFixed.isChecked = it.fixed
                        if (MoneyType.EXPENSE.name == it.type) {
                            rBtnExpense.isChecked = true
                        } else {
                            rBtnIncome.isChecked = true
                        }
                        autocompleteSubtype.setText(it.subtype)
                    }
                }
            }
        }

        moneyViewModel.subtypesFiltered.observe(viewLifecycleOwner) {
            if (this::adapterSubtypes.isInitialized) {
                adapterSubtypes.clear()
                adapterSubtypes.addAll(it)
            }
        }

        moneyViewModel.moneyFormState.observe(viewLifecycleOwner) { formState ->
            if (formState.titleError != null) {
                binding.inTitle.error = getString(formState.titleError)
            } else {
                binding.inTitle.error = null
            }
            if (formState.valueError != null) {
                binding.inValue.error = getString(formState.valueError)
            } else {
                binding.inValue.error = null
            }
            if (formState.typeError != null) {
                showToast(requireContext(), formState.typeError)
            }
            if (formState.payDateError != null) {
                binding.inPayDate.error = getString(formState.payDateError)
            } else {
                binding.inPayDate.error = null
            }
            if (formState.dueDayError != null) {
                binding.inDueDay.error = getString(formState.dueDayError)
            } else {
                binding.inDueDay.error = null
            }
        }
    }

    private fun fetchData() {
        moneyViewModel.getMoneyItemById(itemId)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initScreen() {
        if (!isTablet || itemId > 0 || newItem) {
            activity?.hideView(binding.emptyView)
            activity?.showView(binding.coordinator)
        }

        binding.edtTitle.apply {

            changeHintOnFocusChange(activity, getString(R.string.title), "")

            afterTextChanged {
                binding.inTitle.error = null
            }
        }

        binding.edtDescription.changeHintOnFocusChange(
            activity,
            getString(R.string.hint_description),
            ""
        )

        binding.edtValue.apply {

            changeHintOnFocusChange(activity, getString(R.string.hint_value), "")

            afterTextChanged {
                binding.inValue.error = null
            }
        }

        binding.edtPayDate.apply {

            changeHintOnFocusChange(activity, getString(R.string.hint_date), "")

            afterTextChanged {
                binding.inPayDate.error = null
            }
        }

        binding.edtDueDay.apply {

            changeHintOnFocusChange(activity, getString(R.string.hint_day), "")

            afterTextChanged {
                binding.inDueDay.error = null
            }
        }

        binding.apply {

            rGrpType.setOnCheckedChangeListener { _, _ ->
                if (rBtnIncome.isChecked) {
                    chkPaid.text = getString(R.string.received)
                    inPayDate.hint = getString(R.string.reception_date)
                    inDueDay.hint = getString(R.string.reception_day)
                } else {
                    chkPaid.text = getString(R.string.paid)
                    inPayDate.hint = getString(R.string.pay_date)
                    inDueDay.hint = getString(R.string.due_day)
                }
            }

            chkPaid.setOnCheckedChangeListener { _, checked ->
                if (checked) {
                    activity?.showView(inPayDate)
                } else {
                    activity?.hideView(inPayDate)
                }
            }

            chkFixed.setOnCheckedChangeListener { _, checked ->
                if (checked) {
                    activity?.showView(inDueDay)
                } else {
                    activity?.hideView(inDueDay)
                }
            }

            adapterSubtypes = ArrayAdapter<String>(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                ArrayList()
            )
            autocompleteSubtype.setAdapter(adapterSubtypes)
            autocompleteSubtype.apply {
                afterTextChanged {
                    moneyViewModel.getSubtypesByUserAndFilter(
                        moneyViewModel.getCurrentUserEmail()!!, it
                    )
                }
            }

            edtValue.addTextChangedListener(
                MaskUtil.mask(
                    edtValue,
                    null
                )
            )
            edtPayDate.addTextChangedListener(MaskUtil.mask(edtPayDate, MaskUtil.FORMAT_DATE))

            edtPayDate.setOnTouchListener(View.OnTouchListener { _, motionEvent ->
                //val drawableLeft = 0
                //val drawableTop = 1
                val drawableRight = 2
                //val drawableBottom = 3

                if (motionEvent.action == MotionEvent.ACTION_UP) {
                    if (motionEvent.rawX >= edtPayDate.right - edtPayDate.compoundDrawables[drawableRight].bounds.width()) {

                        val datePickerBuilder = MaterialDatePicker.Builder.datePicker()
                        datePickerBuilder.setTitleText(R.string.select_the_date)

                        val payDateString = binding.edtPayDate.text.toString()
                        if (payDateString.isNotEmpty()) {
                            try {
                                val payDate = DateUtil.DAY_MONTH_YEAR.parse(payDateString)
                                if (payDate != null) {
                                    if (!DateUtil.DAY_MONTH_YEAR.format(payDate)
                                            .equals(payDateString)
                                    ) {
                                        money?.let {
                                            if (it.payDate != null) {
                                                datePickerBuilder.setSelection(it.payDate?.time)
                                            }
                                        }
                                    }
                                    datePickerBuilder.setSelection(payDate.time)
                                } else {
                                    money?.let {
                                        if (it.payDate != null) {
                                            datePickerBuilder.setSelection(it.payDate?.time)
                                        }
                                    }
                                }
                            } catch (e: Exception) {
                                money?.let {
                                    if (it.payDate != null) {
                                        datePickerBuilder.setSelection(it.payDate?.time)
                                    }
                                }
                            }
                        } else {
                            money?.let {
                                if (it.payDate != null) {
                                    datePickerBuilder.setSelection(it.payDate?.time)
                                }
                            }
                        }

                        val datePicker = datePickerBuilder.build()

                        datePicker.addOnPositiveButtonClickListener {
                            edtPayDate.setText(DateUtil.DAY_MONTH_YEAR.format(Date(it)))
                        }

                        datePicker.show(childFragmentManager, "PAY_DATE")
                        return@OnTouchListener true
                    }
                }
                return@OnTouchListener false
            })

            if (itemId > 0 && !editItem) {
                fabSaveOrEdit.setImageResource(R.drawable.ic_baseline_edit_24)
                enableViews(layoutAddItemParent, false)
                changeDrawableColorOfTheEditText(edtPayDate, false)
            } else {
                setupFabAppearance(
                    fabSaveOrEdit,
                    R.drawable.ic_baseline_done_24,
                    R.color.white,
                    R.color.colorPrimary
                )
            }

            fabSaveOrEdit.setOnClickListener {
                if (moneyViewModel.isMoneyFormDataValid(
                        binding.edtTitle.text.toString(),
                        binding.edtValue.text.toString(),
                        binding.rBtnIncome.isChecked,
                        binding.rBtnExpense.isChecked,
                        binding.chkPaid.isChecked,
                        binding.edtPayDate.text.toString(),
                        binding.chkFixed.isChecked,
                        binding.edtDueDay.text.toString()
                    )
                ) {
                    if (itemId > 0) {
                        if (update) {
                            // Update
                            val moneyItem = getMoneyItem()
                            moneyViewModel.updateMoneyItem(moneyItem)
                            if (isTablet) {
                                activity?.showView(binding.emptyView)
                                activity?.hideView(binding.coordinator)
                                clearMoneyItem()
                            } else {
                                activity?.onBackPressed()
                            }
                        } else {
                            update = true
                            enableViews(layoutAddItemParent, true)
                            changeDrawableColorOfTheEditText(edtPayDate, true)
                            setupFabAppearance(
                                fabSaveOrEdit,
                                R.drawable.ic_baseline_done_24,
                                R.color.white,
                                R.color.colorPrimary
                            )
                            rotateView(fabSaveOrEdit)
                        }
                    } else {
                        // Save
                        val moneyItem = getMoneyItem()
                        moneyViewModel.addMoneyItem(moneyItem)
                        if (isTablet) {
                            activity?.showView(binding.emptyView)
                            activity?.hideView(binding.coordinator)
                            clearMoneyItem()
                        } else {
                            activity?.onBackPressed()
                        }
                    }
                }
            }

            fabCancel.setOnClickListener {
                if (isTablet) {
                    activity?.showView(binding.emptyView)
                    activity?.hideView(binding.coordinator)
                    clearMoneyItem()
                } else {
                    activity?.onBackPressed()
                }
            }
        }
    }

    private fun getMoneyItem(): Money {
        var payDate: Date? = null
        var dueDay: Int? = null

        val payDateString = binding.edtPayDate.text.toString()
        val dueDayString = binding.edtDueDay.text.toString()

        if (payDateString.isNotEmpty()) {
            try {
                payDate = DateUtil.DAY_MONTH_YEAR.parse(payDateString)
            } catch (e: Exception) {
                // do nothing
            }
        }
        if (dueDayString.isNotEmpty()) {
            try {
                dueDay = dueDayString.toInt()
            } catch (e: Exception) {
                // do nothing
            }
        }

        val valueString = binding.edtValue.text.toString()

        val value = BigDecimal(MaskUtil.parseValue(valueString).toString())

        val moneyItem = Money(
            userEmail = moneyViewModel.getCurrentUserEmail()!!,
            title = binding.edtTitle.text.toString(),
            description = binding.edtDescription.text.toString(),
            value = value,
            type = if (binding.rBtnExpense.isChecked) MoneyType.EXPENSE.name else MoneyType.INCOME.name,
            subtype = binding.autocompleteSubtype.text.toString(),
            payDate = payDate,
            paid = binding.chkPaid.isChecked,
            fixed = binding.chkFixed.isChecked,
            dueDay = dueDay
        )
        if (money != null) {
            moneyItem.id = money!!.id
            moneyItem.creationDate = money!!.creationDate
        }
        return moneyItem
    }

    private fun enableViews(layout: ViewGroup, enabled: Boolean) {
        layout.isEnabled = enabled
        for (i in 0 until layout.childCount) {
            val child = layout.getChildAt(i)
            if (child is ViewGroup) {
                enableViews(child, enabled)
            } else {
                child.isEnabled = enabled
            }
        }
    }

    private fun changeDrawableColorOfTheEditText(editText: EditText, enabled: Boolean) {
        val typedValue = TypedValue()
        requireContext().theme.resolveAttribute(android.R.attr.colorControlNormal, typedValue, true)
        val typedArray = requireContext().theme.obtainStyledAttributes(
            typedValue.data,
            intArrayOf(android.R.attr.colorControlNormal)
        )
        val colorControlNormal = typedArray.getColor(0, -1)
        val color = if (enabled) {
            ContextCompat.getColor(requireContext(), R.color.colorPrimary)
        } else {
            colorControlNormal
        }
        editText.post {
            val drawables = editText.compoundDrawables
            for (i in drawables.indices) {
                val icon = drawables[i]
                icon?.let {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        icon.colorFilter = BlendModeColorFilter(color, BlendMode.SRC_IN)
                    } else {
                        @Suppress("DEPRECATION")
                        icon.setColorFilter(color, PorterDuff.Mode.SRC_IN)
                    }
                    when (i) {
                        0 -> editText.setCompoundDrawablesWithIntrinsicBounds(
                            icon,
                            null,
                            null,
                            null
                        )
                        1 -> editText.setCompoundDrawablesWithIntrinsicBounds(
                            null,
                            icon,
                            null,
                            null
                        )
                        2 -> editText.setCompoundDrawablesWithIntrinsicBounds(
                            null,
                            null,
                            icon,
                            null
                        )
                        3 -> editText.setCompoundDrawablesWithIntrinsicBounds(
                            null,
                            null,
                            null,
                            icon
                        )
                        else -> editText.setCompoundDrawablesWithIntrinsicBounds(
                            null,
                            null,
                            null,
                            null
                        )
                    }
                    editText.setCompoundDrawablesWithIntrinsicBounds(null, null, icon, null)
                }
            }
        }
    }

    private fun setupFabAppearance(
        fabSaveOrEdit: FloatingActionButton,
        @DrawableRes iconDrawable: Int,
        @Nullable @ColorRes iconColor: Int?,
        @ColorRes backgroundColor: Int
    ) {
        if (iconColor != null) {
            val imgDrawable = AppCompatResources.getDrawable(requireContext(), iconDrawable)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                imgDrawable?.colorFilter =
                    BlendModeColorFilter(requireContext().getColor(iconColor), BlendMode.SRC_IN)
            } else {
                @Suppress("DEPRECATION")
                imgDrawable?.setColorFilter(
                    ContextCompat.getColor(requireContext(), iconColor),
                    PorterDuff.Mode.SRC_IN
                )
            }
            fabSaveOrEdit.setImageDrawable(imgDrawable)
        } else {
            fabSaveOrEdit.setImageResource(iconDrawable)
        }
        fabSaveOrEdit.backgroundTintList =
            ColorStateList.valueOf(ContextCompat.getColor(requireContext(), backgroundColor))
    }

    private fun rotateView(view: View) {
        ViewCompat.animate(view)
            .rotation(360.0f)
            .withLayer()
            .setDuration(500L)
            .setInterpolator(OvershootInterpolator(10.0f))
            .start()
    }
}