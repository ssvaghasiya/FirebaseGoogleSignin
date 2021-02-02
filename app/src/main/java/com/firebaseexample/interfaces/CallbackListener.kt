package com.firebaseexample.interfaces

interface CallbackListener {

    fun onSuccess()

    fun onCancel()

    fun onRetry()
}