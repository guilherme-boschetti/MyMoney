package com.guilhermeb.mymoney.view.money.activity.incomeexpensesubtype
/*
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.guilhermeb.mymoney.R
import com.guilhermeb.mymoney.common.enum.MoneyType
import com.guilhermeb.mymoney.common.extension.changeHintOnFocusChange
import com.guilhermeb.mymoney.databinding.ActivityAddIncomeOrExpenseSubtypeBinding
import com.guilhermeb.mymoney.model.data.local.room.database.MyMoneyDB
import com.guilhermeb.mymoney.model.data.local.room.entity.money.incomeexpensesubtype.IncomeExpenseSubtype
import com.guilhermeb.mymoney.view.app.activity.AbstractActivity
import com.guilhermeb.mymoney.viewmodel.money.incomeexpensesubtype.IncomeExpenseSubtypeViewModel
import com.guilhermeb.mymoney.viewmodel.money.incomeexpensesubtype.IncomeExpenseSubtypeViewModelFactory

class AddIncomeOrExpenseSubtypeActivity : AbstractActivity() {

    private val dataSource by lazy {
        MyMoneyDB.getInstance(this@AddIncomeOrExpenseSubtypeActivity).incomeExpenseSubtypeDao
    }
    private val incomeExpenseSubtypeViewModel by lazy {
        ViewModelProvider(
            this@AddIncomeOrExpenseSubtypeActivity,
            IncomeExpenseSubtypeViewModelFactory(dataSource)
        )[IncomeExpenseSubtypeViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityAddIncomeOrExpenseSubtypeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.edtSubtype.changeHintOnFocusChange(this, getString(R.string.subtype), "")

        binding.btnCancel.setOnClickListener {
            //finish()
            incomeExpenseSubtypeViewModel.getAllSubtypesByType(MoneyType.EXPENSE.name)
        }

        binding.btnSave.setOnClickListener {
            incomeExpenseSubtypeViewModel.addSubtype(
                IncomeExpenseSubtype(
                    type = MoneyType.EXPENSE.name,
                    subtype = "Test"
                )
            )
        }

        // == -- ==

        incomeExpenseSubtypeViewModel.subtypes.observe(this) {
            //Log.i("TEST", it.toString())
        }
    }
}
*/