package com.example.blinkshop.models

import com.example.blinkshop.UserRole

data class DeliveryPartner(
    val email: String,
    val name: String,
    val delId: String,
    val role: UserRole,
    val isActivate: Boolean,
    val password: String,
    val phoneNumber: String,
    val liveLocationLatitude: Double,
    val liveLocationLongitude: Double,
    val address: String
)
