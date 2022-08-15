package com.guilhermeb.mymoney.model.data.remote.firebase

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.guilhermeb.mymoney.model.data.local.room.entity.money.Money
import com.guilhermeb.mymoney.model.repository.contract.AsyncProcess
import java.math.BigDecimal
import java.util.*
import kotlin.collections.ArrayList

class FirebaseRealTimeDataBase {

    companion object {
        const val ITEMS = "items"
    }

    init {
        val database = Firebase.database
        database.setPersistenceEnabled(true) // Offline Capabilities
        val itemsRef = database.getReference(ITEMS)
        itemsRef.keepSynced(true) // Keeping Data Fresh
    }

    private val databaseReference: DatabaseReference by lazy {
        Firebase.database.reference
    }

    private fun getUserEmailForChild(userEmail: String): String {
        return userEmail.replace(".", "_")
    }

    private fun setValueForUserEmailChildAndItemIdChild(moneyItem: Money) {
        val userEmailChild = getUserEmailForChild(moneyItem.userEmail)
        val itemId = moneyItem.id.toString()

        databaseReference.child(ITEMS).child(userEmailChild).child(itemId)
            .setValue(MoneyItem(moneyItem))
    }

    fun insert(moneyItem: Money) {
        setValueForUserEmailChildAndItemIdChild(moneyItem)
    }

    fun update(moneyItem: Money) {
        setValueForUserEmailChildAndItemIdChild(moneyItem)
    }

    fun delete(moneyItem: Money) {
        val userEmail = getUserEmailForChild(moneyItem.userEmail)
        val itemId = moneyItem.id.toString()

        databaseReference.child(ITEMS).child(userEmail).child(itemId).removeValue()
    }

    fun removeAllMoneyItemsByUser(userEmail: String) {
        val userEmailChild = getUserEmailForChild(userEmail)

        databaseReference.child(ITEMS).child(userEmailChild).removeValue()
    }

    fun fetchDataFromFirebaseRTDB(userEmail: String, asyncProcess: AsyncProcess<List<Money>>) {
        val userEmailChild = getUserEmailForChild(userEmail)

        databaseReference.child(ITEMS).child(userEmailChild).get().addOnSuccessListener {
            val moneyItems = it.getValue<List<MoneyItem?>?>()

            moneyItems?.let {
                val items = ArrayList<Money>()
                for (moneyItem in moneyItems) {
                    moneyItem?.let {
                        items.add(
                            Money(
                                moneyItem.id,
                                moneyItem.userEmail,
                                moneyItem.title,
                                moneyItem.description,
                                BigDecimal(moneyItem.value.toString()),
                                moneyItem.type,
                                moneyItem.subtype,
                                moneyItem.payDate,
                                moneyItem.paid,
                                moneyItem.fixed,
                                moneyItem.dueDay,
                                moneyItem.creationDate
                            )
                        )
                    }
                }

                asyncProcess.onComplete(true, items)
            }
        }.addOnFailureListener {
            asyncProcess.onComplete(false, null)
        }
    }
}

data class MoneyItem(
    var id: Long = 0,
    var userEmail: String,
    var title: String,
    var description: String? = null,
    var value: Double,
    var type: String,
    var subtype: String? = null,
    var payDate: Date? = null,
    var paid: Boolean,
    var fixed: Boolean,
    var dueDay: Int? = null,
    var creationDate: Date = Date()
) {
    constructor(money: Money) : this(
        money.id,
        money.userEmail,
        money.title,
        money.description,
        money.value.toDouble(),
        money.type,
        money.subtype,
        money.payDate,
        money.paid,
        money.fixed,
        money.dueDay,
        money.creationDate
    )

    @Suppress("unused")
    constructor() : this(
        0,
        "",
        "",
        null,
        0.0,
        "",
        null,
        null,
        false,
        false,
        null
    )
}