package com.example.blinkshop.roomdb

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "CartProductsTable")
data class CartProducts(
    @PrimaryKey
    val productId: String ="random", //can't apply nullability check here
    val productTitle: String ? = null,
    val productQuantity: String ?=null,
    var productPrice:String ? =null,
    var productCount:Int ? =null,
    var productStock:Int ? =null,
    var productImage:String ? = null,
    var productCategory:String ? = null,
    var adminUid:String ? = null,
    var productType :String ? = null
)