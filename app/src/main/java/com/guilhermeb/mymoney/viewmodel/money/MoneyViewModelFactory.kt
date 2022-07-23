package com.guilhermeb.mymoney.viewmodel.money
/*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MoneyViewModelFactory(private val resetInstance: Boolean) : ViewModelProvider.Factory {

    companion object {
        @Volatile
        private var moneyViewModelInstance: MoneyViewModel? = null

        */
/**
         * Singleton for activity and fragments share the same MoneyViewModel
         *//*

        @Synchronized
        private fun getMoneyViewModelInstance(resetInstance: Boolean): MoneyViewModel {
            if (moneyViewModelInstance == null || resetInstance) {
                moneyViewModelInstance = MoneyViewModel()
            }
            return moneyViewModelInstance!!
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MoneyViewModel::class.java))
            return getMoneyViewModelInstance(resetInstance) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}*/