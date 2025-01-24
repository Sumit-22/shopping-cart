//package com.example.blinkshop.api
//
//import com.example.blinkshop.models.CheckStatus
//import retrofit2.Response
//import retrofit2.http.GET
//import retrofit2.http.HeaderMap
//import retrofit2.http.Headers
//import retrofit2.http.Path
//
//interface ApiInterface {
//    @GET("apis/pg-sandbox/pg/v1/status/{merchantId}/{transactionId}")  //https://api-preprod.phonepe.com/apis/pg-sandbox/pg/v1/status/{merchantId}/{merchantTransactionId} this end point can found in this website :- https://developer.phonepe.com/v1/reference/check-status-api-3/
//    suspend fun checkStatus(
//        @HeaderMap headers : Map<String , String>,
//        @Path("merchantId") merchantId : String,
//        @Path("transactionId") transactionId : String
//    ) : Response<CheckStatus>
//}
///*
//package com.example.blinkshop.activity
//
//import android.app.Activity
//import android.content.Intent
//import android.os.Build
//import android.os.Bundle
//import android.util.Base64
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.widget.Toast
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.activity.viewModels
//import androidx.appcompat.app.AlertDialog
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.content.ContextCompat
//import androidx.lifecycle.lifecycleScope
//import com.example.blinkshop.CartListener
//import com.example.blinkshop.Constants
//import com.example.blinkshop.R
//import com.example.blinkshop.Utils
//import com.example.blinkshop.adapters.AdapterCartProducts
//import com.example.blinkshop.databinding.ActivityOrderPlaceBinding
//import com.example.blinkshop.databinding.AddressLayoutBinding
//import com.example.blinkshop.models.Orders
//import com.example.blinkshop.viewmodels.userViewModel
//import com.phonepe.intent.sdk.api.B2BPGRequest
//import com.phonepe.intent.sdk.api.B2BPGRequestBuilder
//import com.phonepe.intent.sdk.api.PhonePe
//import com.phonepe.intent.sdk.api.PhonePeInitException
//import com.phonepe.intent.sdk.api.models.PhonePeEnvironment
//import com.phonepe.intent.sdk.api.models.PhonePeEnvironment.SANDBOX
//import com.razorpay.Checkout
//import com.razorpay.PaymentData
//import com.razorpay.PaymentResultWithDataListener
//import kotlinx.coroutines.flow.collect
//import kotlinx.coroutines.launch
//import org.json.JSONObject
//import java.nio.charset.Charset
//import java.security.MessageDigest
//
//
//class OrderPlaceActivity : AppCompatActivity(), PaymentResultWithDataListener {
//    private lateinit var binding:ActivityOrderPlaceBinding
//
//    private val viewModel: userViewModel by viewModels<userViewModel>()
//
//    private lateinit var adapterCartProducts: AdapterCartProducts
//
//    private lateinit var b2BPGRequest: B2BPGRequest
//
//    private var cartListener : CartListener? = null
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding=ActivityOrderPlaceBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//        setStatusBarColor()
//        backtoUserMainActivity()
//        getAllCartProducts()
//        initializePhonePay()
//        onPlaceOrderClicked()
//    }
//
////
////    private fun initializePhonePay() {
////        val data = JSONObject()
////        PhonePe.init(
////            this,
////            PhonePeEnvironment.SANDBOX,
////            Constants.MERCHANT_ID,
////            ""
////        ) //appId->REQUIRED FOR ONLY PRODUCTION ENVIRONMENT
////        data.put("merchantID", Constants.MERCHANT_ID)
////        data.put("merchantTransactionId", Constants.merchantTransactionId)
////        data.put("merchantUserId",Constants.merchantUserId)
////        data.put("amount", 2000)
////        data.put("mobileNumber", 8092432741)
////        data.put("callbackUrl", "https://webhook.site/55b92ffb-bc16-447b-a146-2c430aa33736")
////        val paymentInstrument = JSONObject()
////        paymentInstrument.put("type", "PAY_PAGE")
////        data.put("paymentInstrument", paymentInstrument)
////        val deviceContext = JSONObject()
////        deviceContext.put("deviceOS", "ANDROID")
////        data.put("deviceContext", deviceContext)  //https://github.com/1902shubh/PhonepeSdkAndroid
////
////
////        val payloadBase64= Base64.encodeToString(data.toString().toByteArray(
////            Charset.defaultCharset()
////        ),Base64.NO_WRAP)
////        Toast.makeText(this, "payload base64 successful", Toast.LENGTH_SHORT).show()
////        Log.d("Rudracoders","onCreate: $payloadBase64")
////
////        //creating checksum or sha 256
////        val checksum=sha256(payloadBase64 + Constants.apiEndPoint+Constants.SALT_KEY) +"###1"//sha256(base64Body + apiEndPoint + salt) + ### + saltIndex
////       //   Construct the B2BPGRequest from the data obtained from my server
////        b2BPGRequest = B2BPGRequestBuilder()
////            .setData(payloadBase64)  //https://developer.phonepe.com/v1/docs/android-pg-sdk-integration-standard check out all the steps
////            .setChecksum(checksum)
////            .setUrl(Constants.apiEndPoint)
////            .build()
////        Toast.makeText(this, "phonepe initialised successful", Toast.LENGTH_SHORT).show()
////
////    }
////
////
////    private fun sha256(input : String):String{ //The sha256 function takes an input string (input) and returns its SHA-256 hash as a hexadecimal string.It uses the MessageDigest class from the Java Cryptography Architecture (JCA) to compute the hash.
////        val bytearrray = input .toByteArray(Charsets.UTF_8)//Conversion to a byte array using UTF-8 encoding.
////        val md= MessageDigest.getInstance("SHA-256")  //Create an instance of MessageDigest with the algorithm "SHA-256".
////        val digest = md.digest(bytearrray)//Compute the hash by calling md.digest(byteArray). This returns a byte array representing the hash.
////        Toast.makeText(this, "SHA256 Request successful", Toast.LENGTH_SHORT).show()
////        return digest.fold("") { str, it -> str +"%02x".format(it)/*Use fold("") to convert the byte array to a hexadecimal string:
////       The fold function accumulates the result by applying the lambda expression to each byte in the array.
////       %02x formats each byte as a two-character hexadecimal value.The resulting string is the SHA-256 hash.
////  */
////        }
////
////    }
//
//
//    private fun onPlaceOrderClicked()  {
//        binding.btnPlaceOrder.setOnClickListener {
//            viewModel.getAddressStatus().observe(this){status->
//                if(status){
//                    //payment work
//                    Toast.makeText(this, "GETPAYMENT CALLED", Toast.LENGTH_SHORT).show()
//                    getPaymentView()
//                }
//                else{
//                    val addressLayoutBinding = AddressLayoutBinding.inflate(LayoutInflater.from(this))
//
//                    val alertDialog = AlertDialog.Builder(this)
//                        .setView(addressLayoutBinding.root)
//                        .create()
//                    alertDialog.show()
//                    addressLayoutBinding.btnAdd.setOnClickListener {
//                        saveAddress(alertDialog,addressLayoutBinding)
//                    }
//                }
//            }
//        }
//    }
////
////    private val phonePayView = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
////        Toast.makeText(this, "resultCode Request called after this", Toast.LENGTH_SHORT).show()
////        if(it.resultCode == 1){
////            checkStatus()
////            Toast.makeText(this, "resultCode Request successful", Toast.LENGTH_SHORT).show()
////
////        }
////    }
//
////    private fun checkStatus() {  //https://developer.phonepe.com/v1/reference/check-status-api-1/
////            Toast.makeText(this@OrderPlaceActivity, "hi archu", Toast.LENGTH_SHORT).show()
////            val xVerify = sha256( "/pg/v1/status/${Constants. MERCHANT_ID}/${Constants.merchantTransactionId} ${Constants.SALT_KEY}" +"###1")  //https://developer.phonepe.com/v1/reference/pay-api-1/
////            val headers = mapOf(
////                "Content-Type" to "application/json",
////                "X-VERIFY" to xVerify,
////                "X-MERCHANT-ID" to Constants.MERCHANT_ID
////            )
////        lifecycleScope.launch{
////            Toast.makeText(this@OrderPlaceActivity, "before checking phonestatus", Toast.LENGTH_SHORT).show()
////            viewModel.checkPaymentStatus(headers)
////            Toast.makeText(this@OrderPlaceActivity, "ending of checking phonestatus", Toast.LENGTH_SHORT).show()
////            viewModel.paymentStatus.collect {status->
////                if(status){
////                    Utils.showToast(this@OrderPlaceActivity,"Payment done")
////
////                    //orders are saved and products are deleted
////                    saveOrder()
////                    viewModel.deleteCartProducts()
////                    viewModel.savingCartItemCount(0)
////                    cartListener?.hideCartLayout()
////
////
////                    Utils.hideDialogue()
////                    startActivity(Intent(this@OrderPlaceActivity,UsersMainActivity::class.java))
////                    finish()
////                }
////                else{
////                    Utils.showToast(this@OrderPlaceActivity,"Payment can't be done")
////                }
////            }
////        }
////    }
//
//
//
//    private fun saveOrder() {
//        viewModel.getAll().observe(this){cartProductList->
//            viewModel.getUserAddress {address->
//                val order =Orders(
//                    orderId = Utils.getRandomId(), orderList = cartProductList,
//                    userAddress = address, orderStatus = 0, orderDate = Utils.getCurrentDate(),
//                    orderingUserId = Utils.getCurrentUserId()
//                )
//                viewModel.saveOrderedProducts(order)
//
//            }
//            for(products in cartProductList ) {
//                val count = products.productCount
//                val stock = products.productStock?.minus(count!!)
//                if (stock != null) {
//                    viewModel.saveProductsAfterCount(stock, products)
//                }
//            }
//        }
//    }
////    private fun getPaymentView() {
////        try {
////            Toast.makeText(this, "calling of phone view", Toast.LENGTH_SHORT).show()
////            val intent = PhonePe.getImplicitIntent(this, b2BPGRequest, "")
////            //        phonePayView.launch(intent)
////            startActivityForResult(intent!!, 1)
////            Toast.makeText(this, "ending of phone view", Toast.LENGTH_SHORT).show()
//////                .let {
//////                    Toast.makeText(this, "calling of phone view", Toast.LENGTH_SHORT).show()
//////                    phonePayView.launch(it)
//////                    Toast.makeText(thi
////        }
////        catch (e : PhonePeInitException){
////            Toast.makeText(this, e.message.toString(), Toast.LENGTH_SHORT).show()
////        }
////    }
//
//    private fun saveAddress(alertDialog: AlertDialog, addressLayoutBinding: AddressLayoutBinding) {
//        Utils.showDialogue( this, "Processing...")
//        val userPinCode = addressLayoutBinding.etPinCode.text.toString()
//        val userPhoneNumber = addressLayoutBinding.etPhoneNumber.text.toString()
//        val userState =addressLayoutBinding.etState.text.toString()
//        val userDistrict = addressLayoutBinding.etDistrict.text.toString()
//        val userAddress = addressLayoutBinding. etDescriptiveAddress.text.toString()
//        val address = "$userAddress,$userDistrict ,$userPinCode,$userState,India ,\n PhoneNo.:-$userPhoneNumber"
//        lifecycleScope.launch{
//            viewModel.saveUserAddress(address)
//            viewModel.saveAddressStatus()
//        }
//        alertDialog.dismiss()
//
//        getPaymentView()
//    }
//
//    private fun backtoUserMainActivity() {
//        binding.tborderFragment.setNavigationOnClickListener{
//            startActivity(Intent(this,UsersMainActivity::class.java))
//            finish()
//        }
//    }
//
//    private fun getAllCartProducts() {
//        viewModel.getAll().observe(this){cartProductList->
//          adapterCartProducts = AdapterCartProducts()
//          binding.rvProductsItems.adapter= adapterCartProducts
//          adapterCartProducts.differ.submitList(cartProductList)
//
//
//            Utils.showToast(this,"Saved...")
//            var totalprice = 0
//            for(products in cartProductList){
//                val price = products.productPrice?.substring(3)?.toInt()
//                val itemCount = products.productCount!!
//                totalprice += (price?.times(itemCount)!!)
//            }
//            binding.tvSubTotal.text = totalprice.toString()
//            if(totalprice > 200){
//                binding.tvDeliveryCharges.text = "Rs.40"
//                totalprice +=40
//            }
//            binding.tvGrandTotal.text = totalprice.toString()
//        }
//    }
//
//
//    private fun setStatusBarColor(){
//        window?.apply {
//            val statusBarColors = ContextCompat.getColor(this@OrderPlaceActivity, R.color.yellow)
//            statusBarColor = statusBarColors
//            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
//                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
//            }
//        }
//    }
//
//
//}
// */