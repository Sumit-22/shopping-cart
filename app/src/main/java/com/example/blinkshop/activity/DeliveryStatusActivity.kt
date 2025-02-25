package com.example.blinkshop.activity

import android.os.Bundle
import android.widget.FrameLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.blinkshop.R
import com.example.blinkshop.databinding.ActivityDeliveryStatusBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.OnMapReadyCallback


class DeliveryStatusActivity : AppCompatActivity(),OnMapReadyCallback {

    private lateinit var binding: ActivityDeliveryStatusBinding
    private lateinit var mMap: GoogleMap
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeliveryStatusBinding.inflate(layoutInflater)
        supportActionBar?.hide()
        setContentView(binding.root)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
        enableEdgeToEdge()
        mapzoom()

    }

    private fun mapzoom() {
        binding.root.findViewById<FrameLayout>(R.id.mapContainer).setOnTouchListener { _, event ->
            when (event.action) {
                android.view.MotionEvent.ACTION_DOWN, android.view.MotionEvent.ACTION_MOVE ->
                    binding.root.requestDisallowInterceptTouchEvent(true)
                android.view.MotionEvent.ACTION_UP ->
                    binding.root.requestDisallowInterceptTouchEvent(false)
            }
            false
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val deliveryLocation = LatLng(26.8467, 80.9462) // Example: Lucknow
        mMap.addMarker(MarkerOptions().position(deliveryLocation).title("Delivery Location"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(deliveryLocation, 15f))
    }
}