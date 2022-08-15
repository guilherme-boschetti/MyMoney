package com.guilhermeb.mymoney.model.data.local.room.entity.money.chart

import androidx.room.ColumnInfo
import java.math.BigDecimal

data class ChartEntry(
    @ColumnInfo(name = "SUBTYPE") var subtype: String?,
    @ColumnInfo(name = "TOTAL") val total: BigDecimal
)
