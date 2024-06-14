package com.example.blinkshop.auth

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.blinkshop.R
import com.example.blinkshop.Utils
import com.example.blinkshop.activity.UsersMainActivity
import com.example.blinkshop.databinding.FragmentOTPBinding
import com.example.blinkshop.models.users
import com.example.blinkshop.viewmodels.AuthViewModel
import kotlinx.coroutines.launch

class OTPFragment : Fragment() {
    private lateinit var binding:FragmentOTPBinding
    private lateinit var userNumber: String
    private val viewModel: AuthViewModel by viewModels()

/*
The by viewModels() delegate is used to create an instance of the AuthViewModel tied to the lifecycle of the fragment.
This ViewModel will survive configuration changes (like screen rotations) and will be shared among different fragments within the same activity.
 */

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentOTPBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        getUserNumber()
        customizingEnteringOTP()
        sendOTP()
        onLoginButtonClicked()
        onbackButtonClick()
        return binding.root
    }

    private fun onLoginButtonClicked() {
        binding.btnLogIn.setOnClickListener {
            Utils.showDialogue(requireContext(),"Signing you...")
            val editTexts = arrayOf(binding.etOTP1,binding.etOTP2,binding.etOTP3,binding.etOTP4,binding.etOTP5,binding.etOTP6)
            val otp = editTexts.joinToString(""){it.text.toString() }
            if (otp.length< editTexts.size){
                Utils.showToast(requireContext(),"Please enter right otp")
            }
            else{
                editTexts.forEach {
                    it.text?.clear(); it.clearFocus()
                }
                verifyOtp(otp)
            }
        }
    }

    private fun verifyOtp(otp: String) {
        val user = users(uid=null, userPhoneNumber =userNumber, userAddress = " ")
        viewModel.signInWithPhoneAuthCredential(otp, userNumber, user)
        lifecycleScope.launch{
            viewModel.isSignedInSuccessfully.collect(){
                if(it){
                    Utils.hideDialogue()
                    Utils.showToast(requireContext(),"Logged In...")
                    startActivity(Intent(requireContext(),UsersMainActivity::class.java))
                    requireActivity().finish()
                }
            }
        }
    }

    private fun sendOTP() {
        Utils.showDialogue(requireContext(),"Sending OTP...")
        viewModel.apply {
            sendOTP(userNumber, requireActivity())
            lifecycleScope.launch {
                otpSent.collect {
                    if (it) {
                        Utils.hideDialogue()
                        Utils.showToast(requireContext(), "OTP sent ...")
                    }
                }
            }
        }
    }

    private fun onbackButtonClick() {
        binding.tbotpfragment.setNavigationOnClickListener{
            findNavController().navigate(R.id.action_OTPFragment_to_signinFragment)
        }
    }

    private fun customizingEnteringOTP() {
        val editTexts = arrayOf(binding.etOTP1,binding.etOTP2,binding.etOTP3,binding.etOTP4,binding.etOTP5,binding.etOTP6)
        for(i in editTexts.indices){
            editTexts[i].addTextChangedListener(object : TextWatcher{
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    if (s?.length == 1){
                        if(i<editTexts.size-1){
                            editTexts[i+1].requestFocus()
                        }
                    }else if(s?.length==0){
//                        editTexts[i].setOnKeyListener { v, keyCode, event ->
//                            if (keyCode == KeyEvent.KEYCODE_DEL && editTexts[i].text?.isEmpty() == true && i > 0) {
//                                editTexts[i - 1].requestFocus()
//                            }
//                            false
//                        }
                        if(i>0){
                            editTexts[i-1].requestFocus()
                        }
                    }
                }


            })
            // Add a listener to handle the case when the user deletes a number

        }
    }

    private fun getUserNumber() {
        val bundle =arguments
        userNumber = bundle?.getString("number").toString()
        binding.tvUserNuber.text=userNumber
    }


    private fun setStatusBarColor(){
        activity?.window?.apply {
            val statusBarColors = ContextCompat.getColor(requireContext(), R.color.yellow)
            statusBarColor = statusBarColors
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }
}