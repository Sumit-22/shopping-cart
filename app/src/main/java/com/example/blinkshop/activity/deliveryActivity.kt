package com.example.blinkshop.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.blinkshop.R

class deliveryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_delivery)

    }
}