package com.example.blinkshop.auth

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.example.blinkshop.R
import com.example.blinkshop.Utils
import com.example.blinkshop.databinding.FragmentSigninBinding

class SigninFragment : Fragment() {
    private lateinit var binding : FragmentSigninBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentSigninBinding.inflate(layoutInflater)
        setStatusBarColor()

        onContinueButtonClick()
        // Inflate the layout for this fragment
        return binding.root


    }

    private fun onContinueButtonClick() {
        binding.btnContinue.setOnClickListener{
            val number  = binding.etUserNumber.text.toString()
            if (number.isEmpty() || number.length !=10 ){
                Utils.showToast(requireContext(),"Please enter valid phone number")
            }
            else{
                val bundle=Bundle()
                bundle.putString("number",number)
                findNavController().navigate(R.id.action_signinFragment_to_OTPFragment,bundle)

            }
        }

    }

    private fun getUserNumber() {
        //addtextchangedinterface
        binding.etUserNumber.addTextChangedListener (object:TextWatcher {
            override fun beforeTextChanged(number: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(number: CharSequence?, start: Int, before: Int, count: Int) {
                val len = number?.length
                if (len == 10) {
                    binding.btnContinue.setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.correctbtncolor
                        )
                    )
                } else {
                    binding.btnContinue.setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.wrongbtncolor
                        )
                    )
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        }
      )

    }
    /* In Kotlin,1> this refers to the current instance of the class or context where it is used.
    2> this@MainActivity:-When you have nested scopes (such as inner classes or lambdas), you can use this@ClassName to explicitly refer to the outer class or context.
    It’s useful when there are name conflicts (e.g., if you have a local variable with the same name as a class property).
    3>requireContext():It is a method available in Android Fragments.It returns the non-null context associated with the fragment.
    If the fragment is not attached to a context (e.g., during certain lifecycle phases), it throws an IllegalStateException.Use it when you need a context within a fragment.
     */

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