package com.example.blinkshop.models

import android.os.Parcelable
import com.example.blinkshop.UserRole
import kotlinx.parcelize.Parcelize

//
//data class DeliveryPartner(
//    val email: String,
//    val name: String,
//    val delId: String,
//    val role: UserRole,
//    val isActivate: Boolean,
//    val password: String,
//    val phoneNumber: String,
//    val liveLocationLatitude: Double,
//    val liveLocationLongitude: Double,
//    val address: String
//)
@Parcelize
data class DeliveryPartner(
    val email: String = "rakesh245@gmail.com",
    val name: String = "Rakesh kumar",
    val delId: String = "DEL12345",
    val role: UserRole = UserRole.DELIVERY_PARTNER,
    val isActivate: Boolean = true,
    val password: String = "de",
    val phoneNumber: String = "01223456789",
    val liveLocationLatitude: Double = 20.2961,  // Bhubaneswar Latitude
    val liveLocationLongitude: Double = 85.8245, // Bhubaneswar Longitude
    val address: String = "Bhubaneswar, Odisha, India"
):Parcelable
