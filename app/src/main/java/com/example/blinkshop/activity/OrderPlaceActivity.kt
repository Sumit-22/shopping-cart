package com.example.blinkshop.activity
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.blinkshop.databinding.ActivityOrderPlaceBinding

import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.blinkshop.CartListener
import com.example.blinkshop.R
import com.example.blinkshop.Utils
import com.example.blinkshop.adapters.AdapterCartProducts
import com.example.blinkshop.databinding.AddressLayoutBinding
import com.example.blinkshop.models.Orders
import com.example.blinkshop.viewmodels.userViewModel
import com.razorpay.Checkout
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import kotlin.system.measureTimeMillis


class OrderPlaceActivity : AppCompatActivity(), PaymentResultWithDataListener {

    private lateinit var binding: ActivityOrderPlaceBinding

   private val viewModel: userViewModel by viewModels<userViewModel>()

    private lateinit var adapterCartProducts: AdapterCartProducts

    private var cartListener : CartListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding=ActivityOrderPlaceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setStatusBarColor()
        backtoUserMainActivity()
        Checkout.preload(this)
        val co = Checkout()
        co.setKeyID("rzp_test_xrwNXqW1Fu0Jcw")
        onPlaceOrderClicked()
        lifecycleScope.launch {
            getAllCartProducts()
        }


    }


    private fun onPlaceOrderClicked()  {
        binding.btnPlaceOrder.setOnClickListener {
            viewModel.getAddressStatus().observe(this){status->
                if(status){
                    //payment work
                    Toast.makeText(this, "GETPAYMENT CALLED", Toast.LENGTH_SHORT).show()
                    getPaymentView()
                }
                else{
                    Toast.makeText(this, "Before address layout CALLED", Toast.LENGTH_SHORT).show()
                    val addressLayoutBinding = AddressLayoutBinding.inflate(LayoutInflater.from(this))
                    val alertDialog = AlertDialog.Builder(this)
                        .setView(addressLayoutBinding.root)
                        .create()
                    alertDialog.show()
                    Toast.makeText(this, "After address layout CALLED", Toast.LENGTH_SHORT).show()
                    addressLayoutBinding.btnAdd.setOnClickListener {
                        saveAddress(alertDialog,addressLayoutBinding)
                        Toast.makeText(this, "Address is saved", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun getPaymentView() {
        val activity: Activity = this
        val co = Checkout()

        try {
            val options = JSONObject()
            options.put("name","Rudra Coders")
            options.put("description","Software development company")
            //You can omit the image option to fetch the image from the dashboard
            options.put("image","http://example.com/image/rzp.jpg")  //for icon
            options.put("theme.color","#3AA3D3")
            options.put("currency","INR")
            //      options.put("order_id", "order_DBJOWzybf0sJbb");
            options.put("amount","50000")//pass amount in currency subunits
            val retryObj = JSONObject()
            retryObj.put("enabled", true)
            retryObj.put("max_count", 4)
            options.put("retry", retryObj)

            val prefill = JSONObject()
            prefill.put("email","sumitgaurav048@gmail.com")
            prefill.put("contact","8092432741")

            options.put("prefill",prefill)
            co.open(activity,options)
        }catch (e: Exception){
            Toast.makeText(activity,"Error in payment: "+ e.message,Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    private fun saveAddress(alertDialog: AlertDialog, addressLayoutBinding: AddressLayoutBinding) {
        Utils.showDialogue( this, "Processing...")
        val userPinCode = addressLayoutBinding.etPinCode.text.toString()
        val userPhoneNumber = addressLayoutBinding.etPhoneNumber.text.toString()
        val userState =addressLayoutBinding.etState.text.toString()
        val userDistrict = addressLayoutBinding.etDistrict.text.toString()
        val userAddress = addressLayoutBinding. etDescriptiveAddress.text.toString()
        val address = "$userAddress,$userDistrict ,$userPinCode,$userState,India ,\n PhoneNo.:-$userPhoneNumber"
        lifecycleScope.launch{
            viewModel.saveUserAddress(address)
            viewModel.saveAddressStatus()
        }
        alertDialog.dismiss()

        getPaymentView()
    }




    private fun backtoUserMainActivity() {
        binding.tborderFragment.setNavigationOnClickListener{
            startActivity(Intent(this,UsersMainActivity::class.java))
            finish()
        }
    }


    private suspend fun getAllCartProducts() {
        viewModel.getAll().collect {cartProductList->
          adapterCartProducts = AdapterCartProducts()
          binding.rvProductsItems.adapter= adapterCartProducts
          adapterCartProducts.differ.submitList(cartProductList)


            Utils.showToast(this,"Saved...")
            var totalprice = 0
            for(products in cartProductList){
                val price = products.productPrice?.substring(4)?.toInt() //format :-Rs. 100 so start from index 4
                val itemCount = products.productCount!!
                totalprice += (price?.times(itemCount)!!)
            }
            binding.tvSubTotal.text = totalprice.toString()
            if(totalprice < 200){
                binding.tvDeliveryCharges.text = "Rs.40"
                totalprice +=40
            }
            binding.tvGrandTotal.text = totalprice.toString()
        }
    }


    private fun setStatusBarColor(){
        window?.apply {
            val statusBarColors = ContextCompat.getColor(this@OrderPlaceActivity, R.color.yellow)
            statusBarColor = statusBarColors
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }

     override fun onPaymentSuccess(p0: String?, p1: PaymentData?) {
            Utils.showToast(this@OrderPlaceActivity,"Payment done")
         lifecycleScope.launch {
                 uploadData()
         }
     }


    private suspend fun uploadData() {
            Utils.showToast(this, "save order called stocks abra ka dabreea1")
          // viewModel.getAll().collect{ cartProductList ->  if(cartProductList.isNotEmpty()){------- "or"
        val cartProductList = viewModel.getAll().first()
                viewModel.getUserAddress { address ->
                    val order = Orders(
                        orderId = Utils.getRandomId(),
                        orderList = cartProductList,
                        userAddress = address,
                        orderStatus = 0,
                        orderDate = Utils.getCurrentDate(),
                        orderingUserId = Utils.getCurrentUserId()
                    )
                    viewModel.saveOrderedProducts(order)
                    //notification
                    lifecycleScope.launch {
                        viewModel.sendNotifications(
                            cartProductList[0].adminUid!!,
                            "Ordered",
                            "Some products has been ordered",
                            this@OrderPlaceActivity
                        )
                    }
                    for (products in cartProductList) {
                        val count = products.productCount
                        val stock = products.productStock?.minus(count!!)
                        if (stock != null) {
                            Utils.showToast(
                                this@OrderPlaceActivity,
                                "save order called stocks abra ka dabreea3"
                            )
                            viewModel.saveProductsAfterCount(stock,products)

                        }
                    }
                    lifecycleScope.launch {
                        withContext(Dispatchers.Main) { // Switch to main thread if needed
                            afterOrderSaved()
                            Utils.showToast(this@OrderPlaceActivity, "products are deleted")
                        }
                    }
                }
            }


    private fun afterOrderSaved() {// own method stuck here
        Log.i("OrderPlaceActivity", "This is an informational message.")

        lifecycleScope.launch {
            viewModel.deleteCartProducts()
            withContext(Dispatchers.Main) {
                afterDeleteCartProducts()
            }
        }

    }

    private fun afterDeleteCartProducts() {
        viewModel.savingCartItemCount(0)
        cartListener?.hideCartLayout()
        Log.i("OrderPlaceActivity", "cartlistener message.") //working
        Utils.hideDialogue()
        startActivity(Intent(this@OrderPlaceActivity,UsersMainActivity::class.java))
        finish()
    }


    override fun onPaymentError(p0: Int, p1: String?, p2: PaymentData?) {
        Utils.showToast(this,"Payment can't be done")
    }


}
