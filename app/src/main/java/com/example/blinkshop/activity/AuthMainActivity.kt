package com.example.blinkshop.activity

import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import com.example.blinkshop.R

class AuthMainActivity : AppCompatActivity() {

    private lateinit var gestureDetector: GestureDetector
    private val gesturesCompleted = mutableSetOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_main)
        setStrictModePolicy()
        gestureInitialised()
    }

    private fun gestureInitialised() {
        gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            private val SWIPE_THRESHOLD = 100
            private val SWIPE_VELOCITY_THRESHOLD = 100

            override fun onFling(
                e1: MotionEvent?,
                e2: MotionEvent,
                velocityX: Float,
                velocityY: Float
            ): Boolean {
                if (e1 != null && e2 != null) {
                    val diffX = e2.x - e1.x
                    val diffY = e2.y - e1.y
                    when {
                        Math.abs(diffX) > Math.abs(diffY) && Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD -> {
                            if (diffX > 0) {
                                addGesture("right")
                            } else {
                                addGesture("left")
                            }
                            return true
                        }
                        Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD -> {
                            if (diffY > 0) {
                                addGesture("down")
                            } else {
                                addGesture("up")
                            }
                            return true
                        }
                    }
                }
                return false
            }
        })
    }

    private fun addGesture(gesture: String) {
        gesturesCompleted.add(gesture)
        checkGestures()
    }

    private fun checkGestures() {
        // Check if all gestures (right, left, up, down) are completed
        if (gesturesCompleted.containsAll(listOf("right", "left", "up", "down"))) {
            openNextScreen()
        }
    }

    private fun openNextScreen() {
        val intent = Intent(this, deliveryActivity::class.java)  // Ensure the class name is correct
        startActivity(intent)
        gesturesCompleted.clear() // Reset for future interactions
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return gestureDetector.onTouchEvent(event) || super.onTouchEvent(event)
    }

    private fun setStrictModePolicy() {
        // Only allow if necessary; could introduce network operations on main thread
        StrictMode.ThreadPolicy.Builder().permitAll().build().also {
            StrictMode.setThreadPolicy(it)
        }
    }
}
