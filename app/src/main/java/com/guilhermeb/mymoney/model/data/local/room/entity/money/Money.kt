package com.guilhermeb.mymoney.model.data.local.room.entity.money

import androidx.room.*
import java.math.BigDecimal
import java.util.*

// https://developer.android.com/training/data-storage/room/defining-data
/*@Entity(
    tableName = "MONEY",
    foreignKeys = [ForeignKey(
        entity = User::class,
        parentColumns = arrayOf("EMAIL"),
        childColumns = arrayOf("USER_EMAIL"),
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["ID", "USER_EMAIL"], unique = true)]
)*/

@Entity(
    tableName = "MONEY",
    indices = [Index(value = ["ID", "USER_EMAIL"], unique = true)]
)
data class Money(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ID")
    var id: Long = 0,

    @ColumnInfo(name = "USER_EMAIL")
    var userEmail: String,

    @ColumnInfo(name = "TITLE")
    var title: String,

    @ColumnInfo(name = "DESCRIPTION")
    var description: String? = null,

    @ColumnInfo(name = "VALUE")
    var value: BigDecimal,

    @ColumnInfo(name = "TYPE")
    var type: String,

    @ColumnInfo(name = "SUBTYPE")
    var subtype: String? = null,

    @ColumnInfo(name = "PAY_DATE")
    var payDate: Date? = null,

    @ColumnInfo(name = "PAID")
    var paid: Boolean,

    @ColumnInfo(name = "FIXED")
    var fixed: Boolean,

    @ColumnInfo(name = "DUE_DAY")
    var dueDay: Int? = null,

    @ColumnInfo(name = "CREATION_DATE")
    var creationDate: Date = Date()
)