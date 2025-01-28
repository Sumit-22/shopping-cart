package com.example.blinkshop.viewmodels

import android.app.Activity
import androidx.lifecycle.ViewModel
import com.example.blinkshop.Utils
import com.example.blinkshop.accessToken
import com.example.blinkshop.models.users
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.concurrent.TimeUnit

class AuthViewModel: ViewModel() {

    private val _verificationID = MutableStateFlow<String?>(null)
    private val _otpSent = MutableStateFlow(false)
    val otpSent = _otpSent //val otpSent:MutableStateFlow<Boolean> = _otpSent istarah ke type k bare m information gather karo


    private var userToken : String?= null

    fun getAdminToken(): String? {
        return userToken
    }


    private val _isSignedInSuccessfully = MutableStateFlow(false)
    val isSignedInSuccessfully =_isSignedInSuccessfully

    private val _isCurrentUser = MutableStateFlow(false)
    val isCurrentUser = _isCurrentUser
    init {
        Utils.getAuthInstance().currentUser?.let {
          _isCurrentUser.value=true
        }
    }

    fun sendOTP(userNumber: String, activity: Activity){
        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            }

            override fun onVerificationFailed(e: FirebaseException) {

            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken,
            ) {
                _verificationID.value=verificationId
                _otpSent.value=true
            }
        }
        val options = PhoneAuthOptions.newBuilder(Utils.getAuthInstance())
            .setPhoneNumber("+91$userNumber") // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(activity) // Activity (for callback binding)
            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun accessKeyProvider(user: users) : String?{
        val accessTokenProvider = accessToken() // Create an instance of accessToken
        val accessKey = accessTokenProvider.getAccessToken() // Call the method on the instanceval headers = HashMap<String, String>()
        user.userToken = accessKey
        return accessKey
    }

    fun signInWithPhoneAuthCredential(otp: String, userNumber: String, user: users) {
        val credential = PhoneAuthProvider.getCredential(_verificationID.value.toString(), otp)


         userToken=accessKeyProvider(user)

        Utils.getAuthInstance().signInWithCredential(credential)
            .addOnCompleteListener { task ->
                user.uid = Utils.getCurrentUserId()
                if (task.isSuccessful) {
                    FirebaseDatabase.getInstance().getReference("AllUsers").child("Users").child(user.uid!!).setValue(user)
                    _isSignedInSuccessfully.value=true
                    val user = task.result?.user
                } else {


                }
            }
    }


}