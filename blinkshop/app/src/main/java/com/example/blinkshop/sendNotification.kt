package com.example.blinkshop

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.blinkshop.viewmodels.AuthViewModel
import org.json.JSONException
import org.json.JSONObject

class sendNotification(
                       private var userFcmToken: String,
                       private var title: String,
                       private var message: String,
                       private var context: Context,

    ) {




    /**   private val viewModel: AdminViewModel by viewModels() cause of error :-
     * The viewModels() delegate is used within a Fragment or an Activity to obtain an instance of a ViewModel. However, in your sendNotification class, you are trying to use viewModels() in a non-Fragment or non-Activity context, which leads to the "Unresolved reference" error.Suppose if i don't want to  paviewmodel into constructor then use this in fun sendNotification() val viewModel = ViewModelProvider(context as ViewModelStoreOwner).get(AdminViewModel::class.java)
     *         val accessKey = viewModel.adminToken
     */


    /**
     * {
     *   "message": {
     *     "topic": "news",
     *     "notification": {
     *       "title": "Breaking News",
     *       "body": "New news story available."
     *     },
     *     "data": {
     *       "story_id": "story_12345"
     *     }
     *   }
     * }
     *
     */
    private var viewModel: AuthViewModel = ViewModelProvider(context as ViewModelStoreOwner).get(AuthViewModel::class.java)

    private var postUrl = "https://fcm.googleapis.com/v1/projects/shopping-cart-80cc4/messages:send"
     fun sendNotification() {
         val requestQueue:RequestQueue = Volley.newRequestQueue(context)
         val jsonObj :JSONObject = JSONObject()
        try {
            val messageObj  =JSONObject()
            val notificationObj =JSONObject()
            val dataObj =JSONObject()
            Log.d("notify","i love .")

            notificationObj.put("title", title)
            notificationObj.put("body", message)

            //           dataObj.put("title", title)
            //           dataObj.put("message", message)

            messageObj.put("token", userFcmToken)
            messageObj.put("notification", notificationObj)
            messageObj.put("data", dataObj)
            jsonObj.put("message", messageObj)
            val request  = object : JsonObjectRequest(Method.POST,postUrl,jsonObj , {
                response->
                println("Notification sent successfully: $response")
            } , {
                volleyError ->
                // Handle error here
                println("Error sending notification: ${volleyError.message}")
            }){
                override fun getHeaders(): Map<String, String> {
                    val headers = mutableMapOf<String, String>()
                    val accessKey = viewModel.getAdminToken()
                    headers["Content-Type"] = "application/json"
                    Log.d("notify","i love nixdsdedffd kicvd cfdvjlfdkjv;fd,.")
                    headers["Authorization"] = "Bearer $accessKey" // Replace with your FCM server key
                    return headers
                }
            }
            requestQueue.add(request)
        }
        catch (e: JSONException){
            Toast.makeText(context, "Error sending notification due to ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}

/**
* JSONObject (org.json.simple)
    Library: org.json.simple
    Usage: JSONObject is part of the org.json.simple library, which is a simple and lightweight library for working with JSON in Java.
    Methods: It provides methods for creating and manipulating JSON objects, such as JSONObject() for creating a new JSON object, put() for adding key-value pairs, and get() for retrieving values by key.

 JsonObject (Gson)

    Library: com.google.gson
    Usage: JsonObject is part of the Gson library, which is a more powerful and flexible library for working with JSON in Java.
    Methods: It provides methods for creating and manipulating JSON objects, such as JsonObject() for creating a new JSON object, addProperty() for adding properties, and get() for retrieving values by key
 */