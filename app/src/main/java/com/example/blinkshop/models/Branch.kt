package com.example.blinkshop.models

data class Branch (
    val branchName: String,
    val branchId: String,
    val locationLatitude: Double,
    val locationLongitude: Double,
    val address: String,
    val DeliveryPartners: String
    )