package com.example.blinkshop.viewmodels

import android.app.Application
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.blinkshop.FCMTokencallback
import com.example.blinkshop.Utils
import com.example.blinkshop.models.Orders
import com.example.blinkshop.models.Product
import com.example.blinkshop.models.bestSellers
import com.example.blinkshop.roomdb.CartProducts
import com.example.blinkshop.roomdb.CartProductsDao
import com.example.blinkshop.roomdb.CartProductsDatabase
import com.example.blinkshop.sendNotification
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

//since here we use sharedpreference so, we can't use viewmodel , we have to use AndroidViewModel

class userViewModel(application: Application) : AndroidViewModel(application){
//initializations
    val sharedPreferences : SharedPreferences = application.getSharedPreferences("My_Pref ", MODE_PRIVATE)
    val cartProductsDao : CartProductsDao = CartProductsDatabase.getDatabaseInstance(application).cartProductsDaof() //abstract fun in database

    private val _paymentStatus =  MutableLiveData<Boolean> (false)
    val paymentStatus = _paymentStatus

    //RoomDB
    suspend fun insertCartProduct(products: CartProducts){
        cartProductsDao.insertCartProduct(products)
    }

    suspend fun updateCartProduct(products: CartProducts){
        cartProductsDao.updateCartProduct(products)
    }

    suspend fun  deleteCartProduct(productId: String){
        cartProductsDao.deleteCartProduct(productId)
    }

    fun getAll():Flow<List<CartProducts>>{
        return cartProductsDao.getAllCartProducts()
    }

    fun deleteCartProducts(){
        cartProductsDao.deleteCartProducts()
    }

 /*   init{
        if(sharedPreferences!= null){
            Log.d("SharedPreferences","My_pref file created successfully")
        }
        else{
            Log.d("SharedPrefences","Failed to create My_Pref file")
        }
    }*/



    //Firebase call

    fun fetchAllProducts(): Flow<List<Product>> = callbackFlow {//The function returns a Flow of a list of Product objects.It uses the callbackFlow builder, which allows creating a flow that emits values asynchronously.
        val db= FirebaseDatabase.getInstance().getReference("Admins")
            .child("AllProducts")
        val eventListener = object: ValueEventListener { //creates an anonymous object that implements the ValueEventListener interface.
            override fun onDataChange(snapshot: DataSnapshot) {
                val products = ArrayList<Product>()
                for(product in snapshot.children){ //It iterates through all the children under the "AllProducts" node.
                    val prod = product.getValue(Product::class.java)//This line adds the deserialized Product object (prod) to the products list.
                    Log.d("VV",prod.toString()+"img${prod?. productImageUris}")
                    products.add(prod!!)
                }
                trySend(products) //products list to the flow. It emits the list of products to any downstream collectors listening to this flow.
            }
            override fun onCancelled(error: DatabaseError) {

            }
        }
        db.addValueEventListener(eventListener)//It sets up a listener to listen for changes in the data at the location represented by the db reference.Whenever data changes (e.g., new products are added or existing products are updated), the onDataChange method of the eventListener will be called.
        awaitClose {//This line defines a callback to be executed when the flow is closed (e.g., when no more collectors are listening). It removes the eventListener from the database reference to stop listening for updates.
            db.removeEventListener(eventListener)//By removing the listener, we stop listening for updates, which is essential for efficiency and avoiding memory leaks.
        }
    }



    fun getAllOrders():Flow<List<Orders>> = callbackFlow{
       val db = FirebaseDatabase.getInstance().getReference("Admins").child("Orders").orderByChild("orderStatus")

        val eventListener = object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val orderList = ArrayList<Orders>()
                for(ordername in snapshot.children){
                    val order = ordername.getValue(Orders::class.java)
                    if(order?.orderingUserId == Utils.getCurrentUserId())
                        orderList.add(order)
                }
                trySend(orderList)
            }

            override fun onCancelled(error: DatabaseError) {

            }

        }
        db.addValueEventListener(eventListener)
        awaitClose{
            db.removeEventListener(eventListener)
        }

        }




    fun getCategoryProduct(category : String) :Flow<List<Product>> = callbackFlow{
        val db = FirebaseDatabase.getInstance().getReference("Admins").child("ProductCategory/${category}")
        val eventListener = object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val products = ArrayList<Product>()
                for(product in snapshot.children){
                    val prod = product.getValue(Product::class.java)
                    products.add(prod!!)
                }
                trySend(products)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        }
        db.addValueEventListener(eventListener)
        awaitClose {
            db.removeEventListener(eventListener)
        }

    }


    fun getOrderedProducts(orderId : String) :Flow<List<CartProducts>> = callbackFlow{
        val db = FirebaseDatabase.getInstance().getReference("Admins").child("Orders/${orderId}")
        val eventListener = object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val order = snapshot.getValue(Orders::class.java)
                trySend(order!!.orderList!!)
            }

            override fun onCancelled(error: DatabaseError) {

            }

        }
        db.addValueEventListener(eventListener)
        awaitClose {
            db.removeEventListener(eventListener)
        }
    }

    fun updateItemCount(product:Product ,itemCount: Int){
        FirebaseDatabase.getInstance().getReference("Admins").child("AllProducts/${product.productRandomId}").child("itemCount").setValue(itemCount)

        FirebaseDatabase.getInstance().getReference("Admins").child("ProductCategory/${product.productCategory}/${product.productRandomId}").child("itemCount").setValue(itemCount)

        FirebaseDatabase.getInstance().getReference("Admins").child("ProductTypes/${product.productType}/${product.productRandomId}").child("itemCount").setValue(itemCount)



    }

    fun saveProductsAfterCount(stock : Int , product : CartProducts){
        FirebaseDatabase.getInstance().getReference("Admins").child("AllProducts/${product.productId}").child("itemCount").setValue(0)
        FirebaseDatabase.getInstance().getReference("Admins").child("ProductCategory/${product.productCategory}/${product.productId}").child("itemCount").setValue(0)
        FirebaseDatabase.getInstance().getReference("Admins").child("ProductTypes/${product.productType}/${product.productId}").child("itemCount").setValue(0)

        FirebaseDatabase.getInstance().getReference("Admins").child("AllProducts/${product.productId}").child("productStock").setValue(stock)
        FirebaseDatabase.getInstance().getReference("Admins").child("ProductCategory/${product.productCategory}/${product.productId}").child("productStock").setValue(stock)
        FirebaseDatabase.getInstance().getReference("Admins").child("ProductTypes/${product.productType}/${product.productId}").child("productStock").setValue(stock)
    }

    fun saveUserAddress(address: String){
        FirebaseDatabase.getInstance().getReference("AllUsers").child("Users").child(Utils.getCurrentUserId()).child("userAddress").setValue(address)
//see error explanation at 7:01:00 vvi
    }

    fun getUserAddress(callback :(String?) -> Unit){
       val db = FirebaseDatabase.getInstance().getReference("AllUsers").child("Users").child(Utils.getCurrentUserId()).child("userAddress")
        db.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val address = snapshot.getValue(String::class.java)
                    callback(address)
                }
                else{
                    callback(null)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    fun saveAddress(address: String){
        FirebaseDatabase.getInstance().getReference("AllUsers").child("Users").child(Utils.getCurrentUserId()).child("userAddress").setValue(address)
    }


    fun logOutUser(){
        FirebaseAuth.getInstance().signOut()
    }


    fun saveOrderedProducts(orders:Orders){
        FirebaseDatabase.getInstance().getReference("Admins").child("Orders").child(orders.orderId!!).setValue(orders)

    }
//sharedPrefences
    fun savingCartItemCount(itemCount : Int){
        sharedPreferences.edit().putInt("itemCount",itemCount).apply()
    }

    fun fetchTotalCartItemCount():MutableLiveData<Int>{
        val totalItemCount = MutableLiveData<Int>()
        totalItemCount.value = sharedPreferences.getInt("itemCount",0)
        return totalItemCount
    }

    fun saveAddressStatus(){
        sharedPreferences.edit().putBoolean("address Status",true).apply()
    }

    fun getAddressStatus():MutableLiveData<Boolean>{
        val status = MutableLiveData<Boolean>()
        status.value = sharedPreferences.getBoolean("address Status",false)
        return status
    }


    fun fetchProductTypesforBooksellers() : Flow<List<bestSellers>> = callbackFlow {
        val db =FirebaseDatabase.getInstance().getReference("Admins/ProductTypes")
        Log.d("productType","hiiiiiii")
        val eventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val productTypeList =ArrayList<bestSellers>()
                for(productType in snapshot.children){
                    val productTypeName = productType.key
                    Log.d("productType","name : ${productTypeName}")
                    val productList = ArrayList<Product>()

                    for (products in productType.children){
                        val product = products.getValue(Product::class.java)
                        productList.add(product!!)
                    }
                    val bestseller = bestSellers(productType = productTypeName , products = productList)
                    productTypeList.add(bestseller)
                    Log.d("productType"," i love you 1st name : ${productTypeList} , bestSelers :${bestseller}")
                }
                trySend(productTypeList)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("productTypeerror"," i love you 2nd name : ${error}")
            }
        }
        db.addValueEventListener(eventListener)
        awaitClose {
            db.removeEventListener(eventListener)
        }
    }


    fun fcmTokenProvider(callback: FCMTokencallback){
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task->
            var currentRetry =0
            val maxRetries =3
            if(!task.isSuccessful){

                println("Fetching FCM registration token failed :${task.exception}")
                callback.onTokenError(task.exception)
                return@addOnCompleteListener
            }
            val fcmToken = task.result
            println("FCM Token: $fcmToken")
            callback.onTokenReceived(fcmToken)
        }
    }


    //NOTIFICATION
     suspend fun sendNotifications(adminUid: String, title: String, message: String,context: Context) {
        val maxRetries = 3 // Maximum number of retries
        var currentRetry = 0
        lateinit var FCMtoken:String
        fcmTokenProvider(object :FCMTokencallback{
            override fun onTokenReceived(token: String) {
                FCMtoken = token

            }

            override fun onTokenError(exception: Exception?) {
                println("Error retrieving FCM token: ${exception?.message}")

                // Retry logic
                if (currentRetry < maxRetries) {
                    currentRetry++
                    println("Retrying to fetch FCM token... Attempt $currentRetry")
                    fcmTokenProvider(this) // Retry fetching the token
                } else {
                    println("Max retries reached. Unable to fetch FCM token.")
                }
            }

        })

        val getToken = FirebaseDatabase.getInstance().getReference("Admins").child("AdminInfo").child(adminUid).child("adminToken").get()
        getToken.addOnCompleteListener {task->
            val token = task.result.getValue(String::class.java)
            val notification: sendNotification = sendNotification(FCMtoken,title,message,context)
            notification.sendNotification()
        }
    }













//    //retrofit
//    suspend fun checkPaymentStatus(headers:Map<String,String>){
//      val response = ApiUtilities.statusApi.checkStatus(headers, Constants.MERCHANT_ID,Constants.merchantTransactionId) // here we use response how we can use callbacks
//        _paymentStatus.value=response.body() != null && response.body()!!.success
//        //  if (response.body() != null && response.body()!!.success){ _paymentStatus.value = true }
//     //   else{ _paymentStatus.value = false }
//
//    }
}
















/*getSharedPreferences is a method provided by the Android framework to access and manage shared preferences.
->> Shared preferences allow you to store and retrieve small amounts of data (key-value pairs) persistently across app sessions.
**  Purpose and Use Cases:
->> Use shared preferences to store user preferences, settings, and other data that needs to be remembered across app launches.
** Common use cases include:
->> Storing user preferences (e.g., theme selection, language preference).
->> Saving login credentials (e.g., username, token).
->> Keeping track of app-specific settings (e.g., sound on/off, notifications).
** Accessing and Modifying Data:
Once you have the SharedPreferences instance, you can:
->> Retrieve values using getString, getInt, getBoolean, etc.
->> Modify values using edit() and putString, putInt, putBoolean, etc.
** Thread Safety:
->> The SharedPreferences object is thread-safe, meaning multiple threads can access it simultaneously.
** val sharedPreferences: SharedPreferences = application.getSharedPreferences("My_Pref", MODE_PRIVATE):
->> This line creates an instance of SharedPreferences named sharedPreferences.
->> It uses the getSharedPreferences method to retrieve or create a shared preference file named “My_Pref”.
->> The MODE_PRIVATE flag ensures that only the app itself can access this shared preference file.
** sharedPreferences.edit().putInt("itemCount", itemCount).apply():
->> This line modifies the shared preferences by editing the sharedPreferences.It retrieves an editor object using edit().
->> The putInt("itemCount", itemCount) method stores an integer value (itemCount) with the key “itemCount” in the shared preferences.
->> The apply() method applies the changes asynchronously (without blocking the UI thread).





Kotlin Flow is a powerful asynchronous data stream that offers several advantages over normal variables and LiveData:

**Advantages of Kotlin Flow over normal variables:**

1. **Asynchronous Data Streams**: Flows can emit multiple asynchronous values over time, unlike a normal variable which can only hold a single value. This makes Flows well-suited for representing streams of data.

2. **Suspension and Coroutines**: Flows are built on top of Kotlin coroutines, allowing them to efficiently handle asynchronous operations without blocking the main thread. This is a key advantage over normal variables.

3. **Composability**: Flows provide a rich set of operators (e.g. map, filter, zip) that allow you to easily compose and transform the data stream, similar to functional programming with collections.

4. **Backpressure Handling**: Flows automatically handle backpressure, ensuring that the consumer can keep up with the producer without losing data or getting overwhelmed.

**Comparison to LiveData:**

1. **Reactive Streams**: Flows are based on the Reactive Streams specification, making them more powerful and flexible than LiveData, which is Android-specific.
2. **Testability**: Flows are easier to test than LiveData, as they don't have the same lifecycle dependencies.
3. **Operators**: Flows provide a wider range of operators (e.g. map, filter, zip) compared to the more limited set available with LiveData.
4. **Threading**: Flows handle threading and concurrency more explicitly and transparently than LiveData, which can sometimes lead to subtle threading issues.
5. **Multiplatform**: Flows are a Kotlin-native construct, making them available on all Kotlin platforms (Android, iOS, server-side, etc.), unlike LiveData which is Android-specific.
In summary, Kotlin Flow offers a more powerful, flexible, and testable way to work with asynchronous data streams compared to both normal variables and Android LiveData. Its reactive nature, coroutine-based design, and rich set of operators make it a compelling choice for modern Android development.

Citations:
[1] https://stackoverflow.com/questions/58773453/kotlin-flow-vs-android-livedata
[2] https://www.youtube.com/watch?v=6Jc6-INantQ
[3] https://developer.android.com/kotlin/flow
[4] https://proandroiddev.com/udf-flowvsrx-a792b946d75c
[5] https://kotlinlang.org/docs/flow.html





Kotlin Flow offers several advantages over MutableStateFlow:

1. **Flexibility**: Regular Flows are more flexible than MutableStateFlow as they can emit multiple values over time, while MutableStateFlow is limited to emitting the current state value[1].

2. **Testability**: Flows are easier to test than MutableStateFlow, as they don't have the same lifecycle dependencies[1].

3. **Operators**: Flows provide a wider range of operators (e.g. map, filter, zip) compared to the more limited set available with MutableStateFlow[1].

4. **Threading**: Flows handle threading and concurrency more explicitly and transparently than MutableStateFlow, which can sometimes lead to subtle threading issues[1].

5. **Backpressure Handling**: Flows automatically handle backpressure, ensuring that the consumer can keep up with the producer without losing data or getting overwhelmed[4].

6. **Multiplatform**: Flows are a Kotlin-native construct, making them available on all Kotlin platforms (Android, iOS, server-side, etc.), unlike MutableStateFlow which is Android-specific[1].

7. **Cold vs Hot**: Regular Flows are *cold*, meaning they work on-demand and provide items only if actively observed. MutableStateFlow is *hot*, meaning it has to exist and do something even if not actively read from, requiring additional maintenance[1].

In summary, while MutableStateFlow is a convenient tool for representing mutable state in Android apps, regular Flows offer more flexibility, testability, and a richer set of operators. The choice between the two depends on the specific needs of the application and the developer's preferences.

Citations:
[1] https://discuss.kotlinlang.org/t/stateflow-vs-flow/25790
[2] https://www.baeldung.com/kotlin/flows-vs-channels
[3] https://proandroiddev.com/udf-flowvsrx-a792b946d75c
[4] https://developer.android.com/kotlin/flow/stateflow-and-sharedflow
[5] https://thoughtbot.com/blog/kotlin-flow-vs-livedata-on-android-what-are-they-why-should-you-care
*/