package com.example.blinkshop.models

data class DeliveryPartner(
    val email: String,
    val name: String,
    val delId: String,
    val role: String,
    val isActivated: Boolean,
    val password: String,
    val phoneNumber: String,
    val liveLocationLatitude: Double,
    val liveLocationLongitude: Double,
    val address: String
)
