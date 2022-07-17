package com.guilhermeb.mymoney.viewmodel.money.incomeexpensesubtype
/*
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guilhermeb.mymoney.model.data.local.room.dao.money.incomeexpensesubtype.IncomeExpenseSubtypeDao
import com.guilhermeb.mymoney.model.data.local.room.entity.money.incomeexpensesubtype.IncomeExpenseSubtype
import kotlinx.coroutines.*

class IncomeExpenseSubtypeViewModel(private val incomeExpenseSubtypeDao: IncomeExpenseSubtypeDao) :
    ViewModel() {

    private val _subtypes = MutableLiveData<List<IncomeExpenseSubtype>>()
    val subtypes: LiveData<List<IncomeExpenseSubtype>> get() = _subtypes

    fun addSubtype(subtype: IncomeExpenseSubtype) {
        viewModelScope.launch {
            incomeExpenseSubtypeDao.insert(subtype)
        }
    }

fun updateSubtype(subtype: IncomeExpenseSubtype) {
        viewModelScope.launch {
            incomeExpenseSubtypeDao.update(subtype)
        }
    }


    fun getAllSubtypesByType(type: String) {
        viewModelScope.launch {
            //_subtypes.value = incomeExpenseSubtypeDao.getAllIncomeExpenseSubtypeByType(type)
            incomeExpenseSubtypeDao.getAllIncomeExpenseSubtypeByType(type).collect {
                _subtypes.value = it
            }
        }
    }
}
*/