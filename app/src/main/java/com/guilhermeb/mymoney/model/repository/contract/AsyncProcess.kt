package com.guilhermeb.mymoney.model.repository.contract

interface AsyncProcess {
    fun onComplete(isSuccessful: Boolean, errorMessage: String?)
}