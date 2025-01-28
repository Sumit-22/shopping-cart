package com.example.blinkshop

interface CartListener {
    fun showCartLayout(itemCount:Int)

    fun savingCartItemCount(itemCount:Int)

    fun getAllCartProducts()

    fun hideCartLayout()
}