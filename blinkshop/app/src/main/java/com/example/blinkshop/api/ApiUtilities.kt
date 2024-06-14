package com.example.blinkshop.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiUtilities {
    val statusApi :ApiInterface by lazy{
        Retrofit.Builder()//you can find link from page
            .baseUrl("https://api-preprod.phonepe.com/apis/pg-sandbox/pg/v1/pay")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiInterface::class.java)
    }
}