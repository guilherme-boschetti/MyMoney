package com.guilhermeb.mymoney.viewmodel.money.chart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.guilhermeb.mymoney.model.data.local.room.entity.money.chart.ChartEntry
import com.guilhermeb.mymoney.viewmodel.money.MoneyViewModel
import java.math.BigDecimal
import javax.inject.Inject

class ChartViewModel @Inject constructor(private val moneyViewModel: MoneyViewModel) : ViewModel() {

    // == -- Money == -- == -- == -- == -- == -- == -- == -- == -- == -- == -- == -- == -- ==

    val selectedYearAndMonthName: LiveData<String> get() = moneyViewModel.selectedYearAndMonthName

    val chartData: LiveData<List<ChartEntry>?> get() = moneyViewModel.chartData

    fun getChartData() {
        moneyViewModel.getChartData()
    }

    fun clearChartData() {
        moneyViewModel.clearChartData()
        _total.value = null
    }

    // == -- Chart == -- == -- == -- == -- == -- == -- == -- == -- == -- == -- == -- == -- ==

    var isPieChartCurrentChart: Boolean = false

    var chartEntries: List<ChartEntry>? = null
        set(value) {
            field = value
            calculateTotal()
        }

    private val _total = MutableLiveData<BigDecimal?>()
    val total: LiveData<BigDecimal?> get() = _total

    private fun calculateTotal() {
        var totalCalc = BigDecimal.ZERO
        chartEntries?.let { entries ->
            //val sum: Double = entries.stream().filter { obj -> Objects.nonNull(obj) }.mapToDouble { obj -> obj.total.toDouble() }.sum()
            //totalCalc = BigDecimal.valueOf(sum)
            for (i in entries.indices) {
                totalCalc = totalCalc.add(entries[i].total)
            }
        }
        _total.value = totalCalc
    }
}