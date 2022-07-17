package com.guilhermeb.mymoney.view.money.fragment

import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.menu.MenuBuilder
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.guilhermeb.mymoney.MyMoneyApplication
import com.guilhermeb.mymoney.R
import com.guilhermeb.mymoney.common.component.AnchoredFabBehavior
import com.guilhermeb.mymoney.common.constant.Constants
import com.guilhermeb.mymoney.common.enum.MoneyType
import com.guilhermeb.mymoney.common.extension.getAndroidTextColorPrimary
import com.guilhermeb.mymoney.common.extension.showConfirmationDialog
import com.guilhermeb.mymoney.common.helper.SharedPreferencesHelper
import com.guilhermeb.mymoney.common.helper.getSharedPreferencesKey
import com.guilhermeb.mymoney.common.util.MaskUtil
import com.guilhermeb.mymoney.databinding.FragmentMoneyItemListBinding
import com.guilhermeb.mymoney.model.data.local.room.entity.money.Money
import com.guilhermeb.mymoney.view.account.activity.AccountActivity
import com.guilhermeb.mymoney.view.app.about.activity.AboutActivity
import com.guilhermeb.mymoney.view.app.settings.activity.SettingsActivity
import com.guilhermeb.mymoney.view.login.activity.LoginActivity
import com.guilhermeb.mymoney.view.money.activity.MoneyHostActivity
import com.guilhermeb.mymoney.view.money.adapter.MoneyItemAdapter
import com.guilhermeb.mymoney.view.money.adapter.RecyclerViewSwipeCallBack
import com.guilhermeb.mymoney.view.money.listener.MoneyItemClickListener
import com.guilhermeb.mymoney.viewmodel.money.MoneyViewModel
import com.guilhermeb.mymoney.viewmodel.money.MoneyViewModelFactory
import java.math.BigDecimal

class MoneyItemListFragment : Fragment(), MoneyItemAdapter.DeleteMoneyItemCallback {

    private var _binding: FragmentMoneyItemListBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private val moneyViewModel by lazy {
        ViewModelProvider(
            this,
            MoneyViewModelFactory(false)
        )[MoneyViewModel::class.java]
    }

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                val data: Intent? = result.data
                data?.let {
                    if (it.hasExtra(Constants.INTENT_EXTRA_KEY_LANGUAGE_CHANGED) &&
                        it.getBooleanExtra(Constants.INTENT_EXTRA_KEY_LANGUAGE_CHANGED, false)
                    ) {
                        activity?.recreate()
                    }
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        buildOptionsMenu()

        _binding = FragmentMoneyItemListBinding.inflate(inflater, container, false)

        if (activity is MoneyHostActivity) {
            (activity as MoneyHostActivity).showBackButton(false)
        }

        return binding.root

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeProperties()
        fetchData()
        initScreen()
    }

    private fun observeProperties() {
        moneyViewModel.selectedYearAndMonthName.observe(viewLifecycleOwner) {
            // Month Year label ----------
            binding.layoutListOfItems.txtMonthYear.text = it
        }

        moneyViewModel.moneyItems.observe(viewLifecycleOwner) {
            val adapter: MoneyItemAdapter =
                binding.layoutListOfItems.recyclerViewMoney.adapter as MoneyItemAdapter
            adapter.submitList(it)

            var totalIncome: BigDecimal = BigDecimal.ZERO
            var totalExpense: BigDecimal = BigDecimal.ZERO
            var totalExpenseFixed: BigDecimal = BigDecimal.ZERO
            var totalExpenseNotFixed: BigDecimal = BigDecimal.ZERO
            var totalExpensePaid: BigDecimal = BigDecimal.ZERO
            var totalExpenseNotPaid: BigDecimal = BigDecimal.ZERO
            var balance: BigDecimal = BigDecimal.ZERO
            for (money in it) {
                if (MoneyType.EXPENSE.name == money.type) {
                    totalExpense = totalExpense.add(money.value)
                    if (money.fixed) {
                        totalExpenseFixed = totalExpenseFixed.add(money.value)
                    } else {
                        totalExpenseNotFixed = totalExpenseNotFixed.add(money.value)
                    }
                    if (money.paid) {
                        totalExpensePaid = totalExpensePaid.add(money.value)
                    } else {
                        totalExpenseNotPaid = totalExpenseNotPaid.add(money.value)
                    }
                } else {
                    totalIncome = totalIncome.add(money.value)
                }
                balance = totalIncome.subtract(totalExpense)
            }
            binding.layoutListOfItems.bottomSheetTotalizer.apply {
                val balanceValueColor = ContextCompat.getColor(
                    context!!,
                    if (balance < BigDecimal.ZERO) {
                        R.color.red
                    } else if (balance > BigDecimal.ZERO) {
                        R.color.app_green
                    } else {
                        R.color.color_text
                    }
                )

                val usePreviousMonthBalance = SharedPreferencesHelper.getInstance()
                    ?.getValue(getSharedPreferencesKey(Constants.PREVIOUS_MONTH_BALANCE), false)
                if (usePreviousMonthBalance != null && usePreviousMonthBalance) {
                    txtBalanceDescription.text =
                        getString(R.string.balance_description_with_previous_month)
                } else {
                    txtBalanceDescription.text = getString(R.string.balance_description)
                }

                txtBalanceValueTitle.setTextColor(balanceValueColor)
                txtBalanceValueTitle.text =
                    MaskUtil.getFormattedCurrencyValueText(balance)
                txtTotalIncomeValue.text =
                    MaskUtil.getFormattedValueText(totalIncome)
                txtTotalExpenseValue.text =
                    MaskUtil.getFormattedValueText(totalExpense)
                txtTotalExpenseFixedValue.text =
                    MaskUtil.getFormattedValueText(totalExpenseFixed)
                txtTotalExpenseNotFixedValue.text =
                    MaskUtil.getFormattedValueText(totalExpenseNotFixed)
                txtTotalExpensePaidValue.text =
                    MaskUtil.getFormattedValueText(totalExpensePaid)
                txtTotalExpenseNotPaidValue.text =
                    MaskUtil.getFormattedValueText(totalExpenseNotPaid)
            }
        }
    }

    private fun fetchData() {
        moneyViewModel.getMoneyItems()
    }

    private fun initScreen() {
        // RecyclerView ----------
        val adapter = MoneyItemAdapter(
            this,
            moneyViewModel.moneyItems,
            binding.guideline != null,
            false,
            binding.root
        )
        binding.layoutListOfItems.recyclerViewMoney.adapter = adapter
        val dividerItemDecoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        binding.layoutListOfItems.recyclerViewMoney.addItemDecoration(dividerItemDecoration)
        // RecyclerView ItemTouchHelper SwipeCallBack
        val itemTouchHelper = ItemTouchHelper(RecyclerViewSwipeCallBack(adapter))
        itemTouchHelper.attachToRecyclerView(binding.layoutListOfItems.recyclerViewMoney)

        // FloatActionButton ----------
        binding.layoutListOfItems.fabAddItem.setOnClickListener(
            MoneyItemClickListener(
                isTablet = binding.guideline != null,
                newItem = true,
                editItem = false,
                rootView = binding.root
            )
        )
        if (binding.guideline == null || Resources.getSystem().configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            val fabLayoutParams =
                binding.layoutListOfItems.fabAddItem.layoutParams as CoordinatorLayout.LayoutParams
            fabLayoutParams.behavior = AnchoredFabBehavior(requireContext(), null)
        }

        // BottomSheet ----------
        val bottomSheetBehavior = BottomSheetBehavior.from(binding.layoutListOfItems.bottomSheet)
        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    binding.layoutListOfItems.bottomSheetTotalizer.imgBottomSheetUpDown.setImageResource(
                        R.drawable.ic_baseline_keyboard_arrow_down_24
                    )
                } else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    binding.layoutListOfItems.bottomSheetTotalizer.imgBottomSheetUpDown.setImageResource(
                        R.drawable.ic_baseline_keyboard_arrow_up_24
                    )
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // do nothing
            }
        })

        binding.layoutListOfItems.bottomSheetTotalizer.layoutBottomSheetHeader.setOnClickListener {
            if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                binding.layoutListOfItems.bottomSheetTotalizer.imgBottomSheetUpDown.setImageResource(
                    R.drawable.ic_baseline_keyboard_arrow_down_24
                )
            } else {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                binding.layoutListOfItems.bottomSheetTotalizer.imgBottomSheetUpDown.setImageResource(
                    R.drawable.ic_baseline_keyboard_arrow_up_24
                )
            }
        }
    }

    private fun buildOptionsMenu() {
        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu, menu)

                // To display icon on overflow menu
                if (menu is MenuBuilder) {
                    menu.setOptionalIconsVisible(true)
                }

                // Change menu icon color programmatically
                menu.apply {
                    for (index in 0 until this.size()) {
                        val item = this.getItem(index)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            item.icon.colorFilter =
                                BlendModeColorFilter(
                                    context?.getAndroidTextColorPrimary() ?: -1,
                                    BlendMode.SRC_IN
                                )
                        } else {
                            @Suppress("DEPRECATION")
                            item.icon.setColorFilter(
                                context?.getAndroidTextColorPrimary() ?: -1,
                                PorterDuff.Mode.SRC_IN
                            )
                        }
                    }
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.menu_about -> {
                        val it = Intent(context, AboutActivity::class.java)
                        startActivity(it)
                        true
                    }
                    R.id.menu_account -> {
                        val it = Intent(context, AccountActivity::class.java)
                        startActivity(it)
                        true
                    }
                    R.id.menu_settings -> {
                        val it = Intent(context, SettingsActivity::class.java)
                        resultLauncher.launch(it)
                        true
                    }
                    R.id.menu_logout -> {
                        context?.showConfirmationDialog(
                            R.string.are_you_sure_you_want_to_logout
                        )
                        {
                            moneyViewModel.signOut()
                            goToLoginActivity()
                        }
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    override fun deleteMoneyItem(moneyItem: Money) {
        moneyViewModel.removeMoneyItem(moneyItem)
        showUndoSnackbar(moneyItem)
    }

    private fun showUndoSnackbar(moneyItem: Money) {
        val snackbar: Snackbar =
            Snackbar.make(
                binding.layoutListOfItems.coordinatorLayout,
                R.string.undo_snack_bar_text,
                Snackbar.LENGTH_LONG
            )
        snackbar.anchorView = binding.layoutListOfItems.bottomSheet
        snackbar.setAction(R.string.undo_snack_bar_button_text) { undoDelete(moneyItem) }
        snackbar.show()
    }

    private fun undoDelete(moneyItem: Money) {
        moneyViewModel.addMoneyItem(moneyItem)
    }

    private fun goToLoginActivity() {
        val intent = Intent(context, LoginActivity::class.java)
        intent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // close the activities on stack
        startActivity(intent)
    }
}