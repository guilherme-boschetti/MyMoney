package com.guilhermeb.mymoney.model.repository.contract

interface AsyncProcess<T> {
    fun onComplete(isSuccessful: Boolean, result: T?)
}