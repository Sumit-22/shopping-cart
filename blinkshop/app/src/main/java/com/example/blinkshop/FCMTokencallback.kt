package com.example.blinkshop

interface FCMTokencallback {
    fun onTokenReceived(token: String)
    fun onTokenError(exception: Exception?)
}