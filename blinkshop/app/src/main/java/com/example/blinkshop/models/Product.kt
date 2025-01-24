package com.example.blinkshop.models

import java.util.UUID
import java.util.concurrent.CountDownLatch

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
    var productImageUris :List<String?> =emptyList()
   )
