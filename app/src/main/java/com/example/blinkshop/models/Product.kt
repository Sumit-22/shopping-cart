package com.example.blinkshop.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
@Parcelize
data class Product(
    var productRandomId :String? = null,
    var productTitle :String ? = null,
    var ProductQuantity :Int ? =null,
    var productUnit :String ? = null,
    var productPrice :Int ? =null,
    var productStock :Int ? =null,
    var productCategory :String ? = null,
    var productType :String ? = null,
    var itemCount :Int ? =null,
    var adminUid :String ? = null,
    @JvmSuppressWildcards
    var productImageUris :List<String?> =emptyList()
   ):Parcelable
