package com.example.blinkshop.viewmodels

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.blinkshop.Constants
import com.example.blinkshop.Utils
import com.example.blinkshop.api.ApiUtilities
import com.example.blinkshop.models.Orders
import com.example.blinkshop.models.Product
import com.example.blinkshop.models.users
import com.example.blinkshop.roomdb.CartProducts
import com.example.blinkshop.roomdb.CartProductsDao
import com.example.blinkshop.roomdb.CartProductsDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import okhttp3.Address

//since here we use sharedpreference so, we can't use viewmodel , we have to use AndroidViewModel

class userViewModel(application: Application) : AndroidViewModel(application){
//initializations
    val sharedPreferences : SharedPreferences = application.getSharedPreferences("My_Pref ", MODE_PRIVATE)
    val cartProductsDao : CartProductsDao = CartProductsDatabase.getDatabaseInstance(application).cartProductsDaof() //abstract fun in database

    private val _paymentStatus = MutableStateFlow<Boolean>(false)
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

    fun getAll():LiveData<List<CartProducts>>{
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
        status.value = sharedPreferences.getBoolean("addressStatus",false)
        return status
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


*/