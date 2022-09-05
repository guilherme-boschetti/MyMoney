package com.guilhermeb.mymoney.model.data.remote.retrofit.currency.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Currency {

    @SerializedName("bid")
    @Expose
    private var bid = 0f

    @SerializedName("ask")
    @Expose
    private var ask = 0f

    fun getBid(): Float {
        return bid
    }

    fun setBid(bid: Float) {
        this.bid = bid
    }

    fun getAsk(): Float {
        return ask
    }

    fun setAsk(ask: Float) {
        this.ask = ask
    }
}