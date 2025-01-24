package com.example.blinkshop.activity

import android.os.Bundle
import android.os.StrictMode
import androidx.appcompat.app.AppCompatActivity
import com.example.blinkshop.R

class AuthMainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setStrictModePolicy()


    }
    private fun setStrictModePolicy() {
        //for Google Auth Library For Java OAuth2 HTTP
        StrictMode.ThreadPolicy.Builder().permitAll().build().also {
            StrictMode.setThreadPolicy(it)
        }
    }

}