package com.example.blinkshop

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.blinkshop.activity.UsersMainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random

class NotificationService : FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage){
        super.onMessageReceived(message)
        val channelID = "UserBlinkit"
        val channel = NotificationChannel(channelID,"Blinkit", NotificationManager.IMPORTANCE_HIGH).apply {
            description="Blinkit messages"
            enableLights(true)
        }
        val manager =getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)

        val pendingIntent = PendingIntent.getActivity(this,0, Intent(this, UsersMainActivity::class.java),PendingIntent.FLAG_UPDATE_CURRENT)

        val notification = NotificationCompat.Builder(this,channelID)
            .setContentTitle(message.data["title"])
            .setContentText(message.data["message"])
            .setSmallIcon(R.drawable.app_icon)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        manager.notify(Random.nextInt(),notification)
    }
}