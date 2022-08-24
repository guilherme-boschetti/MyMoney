package com.guilhermeb.mymoney.view.money.activity.chart

import android.os.Bundle
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.guilhermeb.mymoney.R
import com.guilhermeb.mymoney.common.extension.hideView
import com.guilhermeb.mymoney.common.extension.showView
import com.guilhermeb.mymoney.common.util.MaskUtil
import com.guilhermeb.mymoney.databinding.ActivityChartBinding
import com.guilhermeb.mymoney.model.data.local.room.entity.money.chart.ChartEntry
import com.guilhermeb.mymoney.view.app.activity.AbstractActivity
import com.guilhermeb.mymoney.viewmodel.money.chart.ChartViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject

@AndroidEntryPoint
class ChartActivity : AbstractActivity() {

    private lateinit var chartViewBinding: ActivityChartBinding

    @Inject
    lateinit var chartViewModel: ChartViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        chartViewBinding = ActivityChartBinding.inflate(layoutInflater)
        setContentView(chartViewBinding.root)
        title = getExpensesLabel() // setTitle(R.string.chart)

        observeProperties()
        fetchData()
        handleClicks()
        initDescriptionText()
    }

    override fun onDestroy() {
        super.onDestroy()
        chartViewModel.clearChartData()
    }

    private fun observeProperties() {
        chartViewModel.chartData.observe(this) {
            val chartEntries = updateLabelPercentages(it)
            chartViewModel.chartEntries = chartEntries
            configChart()
        }
        chartViewModel.total.observe(this) {
            chartViewBinding.txtChartDescription.text = getString(
                R.string.chart_description_text,
                MaskUtil.getFormattedCurrencyValueText(it ?: BigDecimal.ZERO)
            )
        }
    }

    private fun fetchData() {
        chartViewModel.getChartData()
    }

    private fun handleClicks() {
        chartViewBinding.imgBtnChangeChart.setOnClickListener {
            configChart()
        }
    }

    private fun initDescriptionText() {
        chartViewBinding.txtChartDescription.text = getString(
            R.string.chart_description_text,
            MaskUtil.getFormattedCurrencyValueText(chartViewModel.total.value ?: BigDecimal.ZERO)
        )
    }

    private fun configChart() {
        if (chartViewModel.isPieChartCurrentChart) {
            setupBarChart(chartViewBinding.barChart, chartViewModel.chartEntries)

            hideView(chartViewBinding.pieChart)
            showView(chartViewBinding.barChart)

            chartViewBinding.imgBtnChangeChart.setImageResource(R.drawable.ic_baseline_pie_chart_24)

            chartViewModel.isPieChartCurrentChart = false
        } else {
            setupPieChart(chartViewBinding.pieChart, chartViewModel.chartEntries)

            hideView(chartViewBinding.barChart)
            showView(chartViewBinding.pieChart)

            chartViewBinding.imgBtnChangeChart.setImageResource(R.drawable.ic_baseline_bar_chart_24)

            chartViewModel.isPieChartCurrentChart = true
        }
    }

    private fun updateLabelPercentages(chartEntries: List<ChartEntry>?): List<ChartEntry>? {
        if (chartEntries != null) {
            var totalSum = BigDecimal.ZERO
            for (chartEntry in chartEntries) {
                totalSum = totalSum.add(chartEntry.total)
            }
            for (chartEntry in chartEntries) {
                val percent = chartEntry.total.multiply(BigDecimal.valueOf(100))
                    .divide(totalSum, 2, RoundingMode.HALF_UP)
                var label = chartEntry.subtype
                label =
                    if (label == null || label.isEmpty()) getString(R.string.no_subtype) else label
                label += " - "
                label += MaskUtil.getFormattedValueText(percent)
                label += "%"
                chartEntry.subtype = label
            }
        }
        return chartEntries
    }

    private fun getExpensesLabel(): String {
        return getString(R.string.expense) + "s - ${chartViewModel.selectedYearAndMonthName.value}"
    }

    private fun setupPieChart(pieChart: PieChart, chartEntries: List<ChartEntry>?) {
        if (chartEntries == null) {
            return
        }

        val pieEntries = ArrayList<PieEntry>()
        for (i in chartEntries.indices) {
            val value = chartEntries[i].total.toFloat()
            val label = chartEntries[i].subtype
            pieEntries.add(PieEntry(value, label))
        }

        val expensesLabel = getExpensesLabel()
        val dataSetLabel = " | $expensesLabel"

        val pieDataSet = PieDataSet(pieEntries, dataSetLabel)
        pieDataSet.colors = ColorTemplate.COLORFUL_COLORS.toMutableList()
        pieDataSet.sliceSpace = 1f

        pieDataSet.valueTextColor = ContextCompat.getColor(this, R.color.white)
        //pieDataSet.valueTextSize = 18f
        pieDataSet.xValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE

        pieChart.data = PieData(pieDataSet)

        pieChart.setEntryLabelColor(ContextCompat.getColor(this, R.color.color_text))

        /*pieChart.centerText = moneyViewModel.selectedYearAndMonthName.value
        pieChart.setCenterTextColor(ContextCompat.getColor(this, R.color.color_text))
        pieChart.setCenterTextSize(24f)
        pieChart.setHoleColor(ContextCompat.getColor(this, R.color.app_screen_background))*/
        pieChart.isDrawHoleEnabled = false

        /*pieChart.description.text = getString(
            R.string.chart_description_text,
            MaskUtil.getFormattedCurrencyValueText(chartViewModel.total.value ?: BigDecimal.ZERO)
        )
        pieChart.description.textSize = 16f
        pieChart.description.textColor = ContextCompat.getColor(this, R.color.color_text)*/
        pieChart.description.isEnabled = false

        //pieChart.legend.textSize = 16f
        //pieChart.legend.textColor = ContextCompat.getColor(this, R.color.color_text)
        pieChart.legend.isEnabled = false

        pieChart.notifyDataSetChanged()
        pieChart.invalidate()
        pieChart.animateXY(1500, 1500)
    }

    private fun setupBarChart(barChart: BarChart, chartEntries: List<ChartEntry>?) {
        if (chartEntries == null) {
            return
        }

        val barEntries = ArrayList<BarEntry>()
        val labels = ArrayList<String?>()
        for (i in chartEntries.indices) {
            val value = chartEntries[i].total.toFloat()
            val label = chartEntries[i].subtype
            barEntries.add(BarEntry(i.toFloat(), value))
            labels.add(label)
        }

        val expensesLabel = getExpensesLabel()
        val dataSetLabel = " | $expensesLabel"

        val barDataSet = BarDataSet(barEntries, dataSetLabel)
        barDataSet.colors = ColorTemplate.COLORFUL_COLORS.toMutableList()
        //barDataSet.valueTextSize = 18f

        barChart.data = BarData(barDataSet)

        barChart.xAxis.granularity = 1f
        barChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        //barChart.xAxis.setLabelCount(labels.size, true)
        //barChart.xAxis.setDrawLabels(true)
        barChart.xAxis.labelCount = labels.size
        if (labels.size > 5) {
            barChart.xAxis.labelRotationAngle = -30f
        }
        //barChart.xAxis.position = XAxis.XAxisPosition.BOTTOM

        /*barChart.description.text = getString(
            R.string.chart_description_text,
            MaskUtil.getFormattedCurrencyValueText(chartViewModel.total.value ?: BigDecimal.ZERO)
        )
        barChart.description.textSize = 16f
        barChart.description.textColor = ContextCompat.getColor(this, R.color.color_text)*/
        barChart.description.isEnabled = false

        //barChart.legend.textSize = 16f
        //barChart.legend.textColor = ContextCompat.getColor(this, R.color.color_text)
        barChart.legend.isEnabled = false

        barChart.notifyDataSetChanged()
        barChart.invalidate()
        barChart.animateXY(1500, 1500)
    }
}