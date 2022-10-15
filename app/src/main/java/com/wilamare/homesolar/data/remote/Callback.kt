package com.wilamare.homesolar.data.remote

interface Callback {
    fun onMessageArrived(message: String)
}