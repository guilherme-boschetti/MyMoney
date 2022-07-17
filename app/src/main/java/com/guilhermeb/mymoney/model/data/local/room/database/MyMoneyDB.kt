package com.guilhermeb.mymoney.model.data.local.room.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.guilhermeb.mymoney.model.data.local.room.dao.money.MoneyDao
import com.guilhermeb.mymoney.model.data.local.room.entity.money.Money
import com.guilhermeb.mymoney.model.data.local.room.database.converter.DatabaseTypeConverters

@Database(
    entities = [Money::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(DatabaseTypeConverters::class)
abstract class MyMoneyDB : RoomDatabase() {

    abstract val moneyDao: MoneyDao

    companion object {
        @Volatile
        private var INSTANCE: MyMoneyDB? = null

        fun getInstance(context: Context): MyMoneyDB {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        MyMoneyDB::class.java,
                        "MY_MONEY_DATABASE"
                    )
                        .fallbackToDestructiveMigration()
                        //.addCallback(callback())
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }

        // https://stackoverflow.com/questions/44697418/how-to-populate-android-room-database-table-on-fisrt-run
        /*private fun callback() = object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                // pre-populate data
                Executors.newSingleThreadExecutor().execute {
                    INSTANCE?.let {
                        CoroutineScope(Dispatchers.IO).launch {
                            it.userDao.insert(User("test@test.com"))
                        }
                    }
                }
            }
        }*/
    }
}
