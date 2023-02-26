package com.guilhermeb.mymoney.model.data.remote.firebase.rtdb

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.guilhermeb.mymoney.model.data.local.room.entity.money.Money
import com.guilhermeb.mymoney.model.data.remote.firebase.authentication.FirebaseAuthentication
import com.guilhermeb.mymoney.model.repository.contract.AsyncProcess
import java.math.BigDecimal
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class FirebaseRealTimeDataBase @Inject constructor(private val firebaseAuthentication: FirebaseAuthentication) {

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

    private fun getUserUidForChild(): String {
        return firebaseAuthentication.getCurrentUserUid()!!
    }

    private fun setValueForUserEmailChildAndItemIdChild(moneyItem: Money) {
        val userUid = getUserUidForChild()
        val itemId = moneyItem.id.toString()

        databaseReference.child(ITEMS).child(userUid).child(itemId)
            .setValue(MoneyItem(moneyItem))
    }

    fun insert(moneyItem: Money) {
        setValueForUserEmailChildAndItemIdChild(moneyItem)
    }

    fun update(moneyItem: Money) {
        setValueForUserEmailChildAndItemIdChild(moneyItem)
    }

    fun delete(moneyItem: Money) {
        val userUid = getUserUidForChild()
        val itemId = moneyItem.id.toString()

        databaseReference.child(ITEMS).child(userUid).child(itemId).removeValue()
    }

    fun removeAllMoneyItemsByUser() {
        val userUid = getUserUidForChild()

        databaseReference.child(ITEMS).child(userUid).removeValue()
    }

    fun fetchDataFromFirebaseRTDB(asyncProcess: AsyncProcess<List<Money>>) {
        val userUid = getUserUidForChild()

        databaseReference.child(ITEMS).child(userUid).get().addOnSuccessListener {
            val items = ArrayList<Money>()
            try {
                // More than one item returns a List
                val moneyItems = it.getValue<List<MoneyItem?>?>()
                moneyItems?.let {
                    for (moneyItem in moneyItems) {
                        moneyItem?.let {
                            items.add(createMoney(moneyItem))
                        }
                    }
                }
            } catch (e: Exception) {
                // Only one item returns a Map
                val mapMoneyItems = it.getValue<Map<String, MoneyItem>?>()
                val entries = mapMoneyItems?.entries
                entries?.let {
                    for (entry in entries) {
                        val moneyItem = entry.value
                        items.add(createMoney(moneyItem))
                    }
                }
            }
            asyncProcess.onComplete(true, items)
        }.addOnFailureListener {
            asyncProcess.onComplete(false, null)
        }
    }

    fun observeMoneyItemsFirebaseRTDB(asyncProcess: AsyncProcess<List<Money>?>) {
        val userUid = getUserUidForChild()

        val moneyItemListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get MoneyItem object(s) and use the value to update the local data base
                try {
                    // More than one item returns a List
                    val moneyItems = dataSnapshot.getValue<List<MoneyItem?>?>()
                    moneyItems?.let {
                        val items = ArrayList<Money>()
                        for (moneyItem in moneyItems) {
                            moneyItem?.let {
                                items.add(createMoney(moneyItem))
                            }
                        }
                        asyncProcess.onComplete(true, items)
                    }
                } catch (e: Exception) {
                    // Only one item
                    val moneyItem = dataSnapshot.getValue<MoneyItem?>()
                    moneyItem?.let {
                        val items = ArrayList<Money>()
                        items.add(createMoney(moneyItem))
                        asyncProcess.onComplete(true, items)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting MoneyItem failed
                Log.e("FirebaseRTDB", databaseError.message)
            }
        }
        databaseReference.child(ITEMS).child(userUid).addValueEventListener(moneyItemListener)
    }

    private fun createMoney(moneyItem: MoneyItem): Money {
        return Money(
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