package com.guilhermeb.mymoney.viewmodel.money

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guilhermeb.mymoney.R
import com.guilhermeb.mymoney.common.enum.MoneyType
import com.guilhermeb.mymoney.common.util.DateUtil
import com.guilhermeb.mymoney.common.util.MaskUtil
import com.guilhermeb.mymoney.model.data.local.room.entity.money.Money
import com.guilhermeb.mymoney.model.data.local.room.entity.money.chart.ChartEntry
import com.guilhermeb.mymoney.model.repository.contract.AsyncProcess
import com.guilhermeb.mymoney.model.repository.money.MoneyRepository
import com.guilhermeb.mymoney.viewmodel.authentication.AuthenticationViewModel
import com.guilhermeb.mymoney.viewmodel.money.state.MoneyFormState
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.util.*
import javax.inject.Inject

class MoneyViewModel @Inject constructor(
    private val moneyRepository: MoneyRepository,
    private val authenticationViewModel: AuthenticationViewModel
) : ViewModel() {

    private val _moneyItems = MutableLiveData<List<Money>>()
    val moneyItems: LiveData<List<Money>> get() = _moneyItems

    private val _moneyItem = MutableLiveData<Money?>()
    val moneyItem: LiveData<Money?> get() = _moneyItem

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

    private val _fetchingDataFromFirebaseRTDB = MutableLiveData<Boolean>()
    val fetchingDataFromFirebaseRTDB: LiveData<Boolean> get() = _fetchingDataFromFirebaseRTDB

    private val _chartData = MutableLiveData<List<ChartEntry>?>()
    val chartData: LiveData<List<ChartEntry>?> get() = _chartData

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

    fun clearMoneyItem() {
        _moneyItem.value = null
    }

    fun getMoneyItems() {
        val userEmail = getCurrentUserEmail()!!

        val startDateAndEndDate = getStartDateAndEndDateOfMonthSelection()
        val startDate = startDateAndEndDate[0]
        val endDate = startDateAndEndDate[1]

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

    private fun getStartDateAndEndDateOfMonthSelection(): Array<String> {
        val calendar = Calendar.getInstance()

        if (_selectedYearAndMonthName.value != null) {
            val yearAndMonthSplit = _selectedYearAndMonthName.value!!.split(" ")
            val year = yearAndMonthSplit[0]
            val monthName = yearAndMonthSplit[1]
            val monthNumber = DateUtil.getMonthNumberByMonthName(monthName)

            calendar.set(Calendar.YEAR, year.toInt())
            calendar.set(Calendar.MONTH, monthNumber - 1)

            _selectedYearAndMonthName.value =
                "${calendar.get(Calendar.YEAR)} ${DateUtil.getMonthNameByMonthNumber(monthNumber)}"
        } else {
            _selectedYearAndMonthName.value = "${calendar.get(Calendar.YEAR)} ${
                DateUtil.getMonthNameByMonthNumber(
                    calendar.get(Calendar.MONTH) + 1
                )
            }"
        }

        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH))
        calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMinimum(Calendar.HOUR_OF_DAY))
        calendar.set(Calendar.MINUTE, calendar.getActualMinimum(Calendar.MINUTE))
        calendar.set(Calendar.SECOND, calendar.getActualMinimum(Calendar.SECOND))
        calendar.set(Calendar.MILLISECOND, calendar.getActualMinimum(Calendar.MILLISECOND))

        val startDate = DateUtil.YEAR_MONTH_DAY_HOURS_MINUTES_SECONDS.format(calendar.time)

        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMaximum(Calendar.HOUR_OF_DAY))
        calendar.set(Calendar.MINUTE, calendar.getActualMaximum(Calendar.MINUTE))
        calendar.set(Calendar.SECOND, calendar.getActualMaximum(Calendar.SECOND))
        calendar.set(Calendar.MILLISECOND, calendar.getActualMaximum(Calendar.MILLISECOND))

        val endDate = DateUtil.YEAR_MONTH_DAY_HOURS_MINUTES_SECONDS.format(calendar.time)

        return arrayOf(startDate, endDate)
    }

    fun fetchDataFromFirebaseRTDB() {
        _fetchingDataFromFirebaseRTDB.value = true
        moneyRepository.fetchDataFromFirebaseRTDB(
            getCurrentUserEmail()!!,
            object : AsyncProcess<List<Money>> {
                override fun onComplete(isSuccessful: Boolean, result: List<Money>?) {
                    result?.let {
                        for (item in result) {
                            addMoneyItem(item)
                        }
                    }
                    _fetchingDataFromFirebaseRTDB.value = false
                }
            })
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

    // == -- Chart == -- == -- == -- == -- == -- == -- == -- == -- == -- == -- == -- == -- ==

    fun getChartData() {
        val userEmail = getCurrentUserEmail()!!

        val type = MoneyType.EXPENSE.name

        val startDateAndEndDate = getStartDateAndEndDateOfMonthSelection()
        val startDate = startDateAndEndDate[0]
        val endDate = startDateAndEndDate[1]

        getChartData(userEmail, type, startDate, endDate)
    }

    private fun getChartData(
        userEmail: String,
        type: String,
        startDate: String,
        endDate: String
    ) {
        viewModelScope.launch {
            moneyRepository.getChartData(userEmail, type, startDate, endDate)
                .collect {
                    _chartData.value = it
                }
        }
    }

    fun clearChartData() {
        _chartData.value = null
    }
}