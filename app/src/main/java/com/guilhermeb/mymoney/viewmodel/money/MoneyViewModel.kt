package com.guilhermeb.mymoney.viewmodel.money

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guilhermeb.mymoney.MyMoneyApplication
import com.guilhermeb.mymoney.R
import com.guilhermeb.mymoney.common.util.DateUtil
import com.guilhermeb.mymoney.common.util.MaskUtil
import com.guilhermeb.mymoney.model.data.local.room.database.MyMoneyDB
import com.guilhermeb.mymoney.model.data.local.room.entity.money.Money
import com.guilhermeb.mymoney.model.repository.money.MoneyRepository
import com.guilhermeb.mymoney.viewmodel.authentication.AuthenticationViewModel
import com.guilhermeb.mymoney.viewmodel.money.state.MoneyFormState
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.util.*

class MoneyViewModel : ViewModel() {

    private val authenticationViewModel by lazy {
        AuthenticationViewModel()
    }
    private val moneyRepository by lazy {
        val dataSource =
            MyMoneyDB.getInstance(MyMoneyApplication.getInstance().applicationContext).moneyDao
        MoneyRepository(dataSource)
    }

    private val _moneyItems = MutableLiveData<List<Money>>()
    val moneyItems: LiveData<List<Money>> get() = _moneyItems

    private val _moneyItem = MutableLiveData<Money>()
    val moneyItem: LiveData<Money> get() = _moneyItem

    private val _subtypesFiltered = MutableLiveData<List<String>>()
    val subtypesFiltered: LiveData<List<String>> get() = _subtypesFiltered

    private val _drawerMonths = MutableLiveData<List<String>>()
    val drawerMonths: LiveData<List<String>> get() = _drawerMonths

    private val _drawerMenuItemChecked = MutableLiveData<Int>()
    val drawerMenuItemChecked: LiveData<Int> get() = _drawerMenuItemChecked

    private val _selectedYearAndMonthName = MutableLiveData<String>()
    val selectedYearAndMonthName: LiveData<String> get() = _selectedYearAndMonthName

    private val _moneyFormState = MutableLiveData<MoneyFormState>()
    val moneyFormState: LiveData<MoneyFormState> = _moneyFormState

    fun addMoneyItem(moneyItem: Money) {
        viewModelScope.launch {
            moneyRepository.insert(moneyItem)
        }
    }

    fun updateMoneyItem(moneyItem: Money) {
        viewModelScope.launch {
            moneyRepository.update(moneyItem)
        }
    }

    fun removeMoneyItem(moneyItem: Money) {
        viewModelScope.launch {
            moneyRepository.delete(moneyItem)
        }
    }

    fun removeAllMoneyItemsByUser(userEmail: String) {
        viewModelScope.launch {
            moneyRepository.removeAllMoneyItemsByUser(userEmail)
        }
    }

    fun getMoneyItemById(id: Long) {
        viewModelScope.launch {
            _moneyItem.value = moneyRepository.getMoneyItemById(id)
        }
    }

    fun getMoneyItems() {
        val userEmail = getCurrentUserEmail()!!

        val calendar = Calendar.getInstance()

        if (_selectedYearAndMonthName.value != null) {
            val yearAndMonthSplit = _selectedYearAndMonthName.value!!.split(" ")
            val year = yearAndMonthSplit[0]
            val monthName = yearAndMonthSplit[1]

            calendar.set(Calendar.YEAR, year.toInt())
            calendar.set(Calendar.MONTH, DateUtil.getMonthNumberByMonthName(monthName) - 1)
        } else {
            _selectedYearAndMonthName.value = "${calendar.get(Calendar.YEAR)} ${
                DateUtil.getMonthNameByMonthNumber(
                    calendar.get(Calendar.MONTH) + 1
                )
            }"
        }

        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH))
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)

        val startDate = DateUtil.YEAR_MONTH_DAY_HOURS_MINUTES_SECONDS.format(calendar.time)

        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)

        val endDate = DateUtil.YEAR_MONTH_DAY_HOURS_MINUTES_SECONDS.format(calendar.time)

        getAllMoneyItemsByUserBetweenDates(userEmail, startDate, endDate)
    }

    private fun getAllMoneyItemsByUserBetweenDates(
        userEmail: String,
        startDate: String,
        endDate: String
    ) {
        viewModelScope.launch {
            moneyRepository.getAllMoneyItemsByUserBetweenDates(userEmail, startDate, endDate)
                .collect {
                    _moneyItems.value = it
                }
        }
    }

    fun getSubtypesByUserAndFilter(userEmail: String, filter: String) {
        viewModelScope.launch {
            moneyRepository.getSubtypesByUserAndFilter(userEmail, filter).collect {
                _subtypesFiltered.value = it
            }
        }
    }

    fun getAllMonthsByUser(userEmail: String) {
        viewModelScope.launch {
            moneyRepository.getAllMonthsByUser(userEmail).collect {
                _drawerMonths.value = getListWithYearAndMonthName(it)
                if (_drawerMenuItemChecked.value == null) {
                    _drawerMenuItemChecked.value = it.indexOf(
                        DateUtil.YEAR_MONTH_DAY_HOURS_MINUTES_SECONDS.format(Date()).substring(0, 7)
                    )
                }
            }
        }
    }

    private fun getListWithYearAndMonthName(listWithYearAndMonth: List<String>): List<String> {
        val listWithYearAndMonthName = mutableListOf<String>()
        for (yearAndMonth in listWithYearAndMonth) {
            val yearAndMonthSplit = yearAndMonth.split("-")
            listWithYearAndMonthName.add(
                "${yearAndMonthSplit[0]} ${
                    DateUtil.getMonthNameByMonthNumber(
                        yearAndMonthSplit[1].toInt()
                    )
                }"
            )
        }
        return listWithYearAndMonthName.toList()
    }

    fun setSelectedYearAndMonthName(selectedYearAndMonthName: String) {
        _selectedYearAndMonthName.value = selectedYearAndMonthName
        getMoneyItems()
    }

    fun setDrawerMenuItemChecked(drawerMenuItemChecked: Int) {
        _drawerMenuItemChecked.value = drawerMenuItemChecked
    }

    // == -- User == -- == -- == -- == -- == -- == -- == -- == -- == -- == -- == -- == -- ==

    fun getCurrentUserEmail(): String? {
        return authenticationViewModel.getCurrentUserEmail()
    }

    fun signOut() {
        authenticationViewModel.signOut()
    }

    // == -- Detail == -- == -- == -- == -- == -- == -- == -- == -- == -- == -- == -- == -- ==

    fun isMoneyFormDataValid(
        title: String,
        value: String,
        typeIncome: Boolean,
        typeExpense: Boolean,
        paid: Boolean,
        payDate: String,
        fixed: Boolean,
        dueDay: String
    ): Boolean {
        if (title.isEmpty()) {
            _moneyFormState.value =
                MoneyFormState(titleError = R.string.title_should_not_be_empty)
        } else if (value.isEmpty()) {
            _moneyFormState.value =
                MoneyFormState(valueError = R.string.value_should_not_be_empty)
        } else if (!typeIncome && !typeExpense) {
            _moneyFormState.value =
                MoneyFormState(typeError = R.string.you_should_choose_the_type)
        } else if (paid && payDate.isEmpty()) {
            _moneyFormState.value =
                MoneyFormState(payDateError = R.string.pay_date_should_not_be_empty)
        } else if (fixed && dueDay.isEmpty()) {
            _moneyFormState.value =
                MoneyFormState(dueDayError = R.string.due_day_should_not_be_empty)
        } else {
            if (value.isNotEmpty()) {
                try {
                    BigDecimal(MaskUtil.parseValue(value).toString())
                } catch (e: Exception) {
                    _moneyFormState.value =
                        MoneyFormState(valueError = R.string.invalid_value)
                    return false
                }
            }

            if (paid && payDate.isNotEmpty()) {
                try {
                    val parsedDate = DateUtil.DAY_MONTH_YEAR.parse(payDate)
                    if (!DateUtil.DAY_MONTH_YEAR.format(parsedDate!!).equals(payDate)) {
                        _moneyFormState.value =
                            MoneyFormState(payDateError = R.string.invalid_pay_date)
                        return false
                    }
                } catch (e: Exception) {
                    _moneyFormState.value =
                        MoneyFormState(payDateError = R.string.invalid_pay_date)
                    return false
                }
            }

            if (fixed && dueDay.isNotEmpty()) {
                try {
                    val dueDayInt = dueDay.toInt()
                    if (dueDayInt < 1 || dueDayInt > 31) {
                        _moneyFormState.value =
                            MoneyFormState(dueDayError = R.string.invalid_due_day_month)
                        return false
                    }
                } catch (e: Exception) {
                    _moneyFormState.value =
                        MoneyFormState(dueDayError = R.string.invalid_due_day)
                    return false
                }
            }

            _moneyFormState.value = MoneyFormState(isDataValid = true)
            return true
        }
        return false
    }
}