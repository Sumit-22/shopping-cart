package com.example.blinkshop.activity

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.blinkshop.CartListener
import com.example.blinkshop.Constants
import com.example.blinkshop.R
import com.example.blinkshop.Utils
import com.example.blinkshop.adapters.AdapterCartProducts
import com.example.blinkshop.databinding.ActivityOrderPlaceBinding
import com.example.blinkshop.databinding.AddressLayoutBinding
import com.example.blinkshop.models.Orders
import com.example.blinkshop.viewmodels.userViewModel
import com.phonepe.intent.sdk.api.B2BPGRequest
import com.phonepe.intent.sdk.api.B2BPGRequestBuilder
import com.phonepe.intent.sdk.api.PhonePe
import com.phonepe.intent.sdk.api.PhonePeInitException
import com.phonepe.intent.sdk.api.models.PhonePeEnvironment
import com.phonepe.intent.sdk.api.models.PhonePeEnvironment.SANDBOX
import com.razorpay.Checkout
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.nio.charset.Charset
import java.security.MessageDigest


class OrderPlaceActivity : AppCompatActivity(), PaymentResultWithDataListener {
    private lateinit var binding:ActivityOrderPlaceBinding

    private val viewModel: userViewModel by viewModels<userViewModel>()

    private lateinit var adapterCartProducts: AdapterCartProducts

    private lateinit var b2BPGRequest: B2BPGRequest

    private var cartListener : CartListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityOrderPlaceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setStatusBarColor()
        backtoUserMainActivity()
        Checkout.preload(this)
        val co = Checkout()
        co.setKeyID("rzp_test_xrwNXqW1Fu0Jcw")
        getPaymentView()
        onPlaceOrderClicked()
        getAllCartProducts()

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
                    val addressLayoutBinding = AddressLayoutBinding.inflate(LayoutInflater.from(this))

                    val alertDialog = AlertDialog.Builder(this)
                        .setView(addressLayoutBinding.root)
                        .create()
                    alertDialog.show()
                    addressLayoutBinding.btnAdd.setOnClickListener {
                        saveAddress(alertDialog,addressLayoutBinding)
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
            options.put("theme.color","#ff1515");
            options.put("currency","INR");
            //      options.put("order_id", "order_DBJOWzybf0sJbb");
            options.put("amount","50000")//pass amount in currency subunits

            val retryObj = JSONObject();
            retryObj.put("enabled", true);
            retryObj.put("max_count", 4);
            options.put("retry", retryObj);

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


    private fun saveOrder() {
        viewModel.getAll().observe(this){cartProductList->
            Utils.showToast(this,"save order called")
            viewModel.getUserAddress {address->
                val order =Orders(
                    orderId = Utils.getRandomId(), orderList = cartProductList,
                    userAddress = address, orderStatus = 0, orderDate = Utils.getCurrentDate(),
                    orderingUserId = Utils.getCurrentUserId()
                )
                viewModel.saveOrderedProducts(order)

            }
            for(products in cartProductList ) {
                val count = products.productCount
                val stock = products.productStock?.minus(count!!)
                if (stock != null) {
                    viewModel.saveProductsAfterCount(stock, products)
                }
            }
        }
    }




    private fun backtoUserMainActivity() {
        binding.tborderFragment.setNavigationOnClickListener{
            startActivity(Intent(this,UsersMainActivity::class.java))
            finish()
        }
    }

    private fun getAllCartProducts() {
        viewModel.getAll().observe(this){cartProductList->
          adapterCartProducts = AdapterCartProducts()
          binding.rvProductsItems.adapter= adapterCartProducts
          adapterCartProducts.differ.submitList(cartProductList)


            Utils.showToast(this,"Saved...")
            var totalprice = 0
            for(products in cartProductList){
                val price = products.productPrice?.substring(3)?.toInt()
                val itemCount = products.productCount!!
                totalprice += (price?.times(itemCount)!!)
            }
            binding.tvSubTotal.text = totalprice.toString()
            if(totalprice > 200){
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
        lifecycleScope.launch{
//            viewModel.paymentStatus.collect {status->
//                if(status){
                    Utils.showToast(this@OrderPlaceActivity,"Payment done")

                    //orders are saved and products are deleted
                    saveOrder()
                    viewModel.deleteCartProducts()
                    Utils.showToast(this@OrderPlaceActivity,"products are deleted")
                    viewModel.savingCartItemCount(0)
                    cartListener?.hideCartLayout()


                    Utils.hideDialogue()
                    startActivity(Intent(this@OrderPlaceActivity,UsersMainActivity::class.java))
                    finish()
                }
            }
        //}
    //}

    override fun onPaymentError(p0: Int, p1: String?, p2: PaymentData?) {
        Utils.showToast(this,"Payment can't be done")
    }
}