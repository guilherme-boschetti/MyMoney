package com.guilhermeb.mymoney.model.data.local.room.database.converter

import androidx.room.TypeConverter
import com.guilhermeb.mymoney.common.util.DateUtil
import java.math.BigDecimal
import java.util.*

class DatabaseTypeConverters {

    @TypeConverter
    fun fromDoubleToBigDecimal(value: Double?): BigDecimal {
        return value?.let { BigDecimal(value.toString()) } ?: BigDecimal.ZERO
    }

    @TypeConverter
    fun fromBigDecimalToDouble(value: BigDecimal?): Double? {
        return value?.let { value.toDouble() }
    }

    @TypeConverter
    fun fromStringToDate(value: String?): Date? {
        return value?.let { DateUtil.YEAR_MONTH_DAY_HOURS_MINUTES_SECONDS.parse(value) }
    }

    @TypeConverter
    fun fromDateToString(value: Date?): String? {
        return value?.let { DateUtil.YEAR_MONTH_DAY_HOURS_MINUTES_SECONDS.format(value) }
    }
}