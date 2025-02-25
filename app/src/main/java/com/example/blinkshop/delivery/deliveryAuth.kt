package com.example.blinkshop.delivery

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil.setContentView
import androidx.navigation.fragment.findNavController
import com.example.blinkshop.R
import com.example.blinkshop.databinding.FragmentDeliveryAuthBinding
import com.example.blinkshop.models.DeliveryPartner


class deliveryAuth : Fragment() {
    private lateinit var binding : FragmentDeliveryAuthBinding

    private fun setAnim() {
        binding.icon.setAnimation(R.raw.delivery_man)
        binding.icon.playAnimation() // Start the animation
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDeliveryAuthBinding.inflate(inflater,container,false)

        checkCredentials(DeliveryPartner())
        setAnim()
        return binding.root
        }

    private fun checkCredentials(partner: DeliveryPartner) {
         binding.loginButton.setOnClickListener {
             val email = binding.emailInput.text.toString().trim()
             val password = binding.passwordInput.text.toString().trim()

             Toast.makeText(requireContext(), partner.email+partner.password, Toast.LENGTH_SHORT).show()
            Toast.makeText(requireContext(), email+password, Toast.LENGTH_SHORT).show()
             if (email.isNotEmpty() && password.isNotEmpty()) {
                 checkCredentials(email, password, partner)
             } else {
                Toast.makeText(requireContext(), "Please enter email and password", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun checkCredentials(email: String, password: String, partner: DeliveryPartner) {
        if (email == partner.email && password == partner.password) {
            Toast.makeText(requireContext(), "Access granted", Toast.LENGTH_SHORT).show()
            val action = deliveryAuthDirections.actionDeliveryAuthToDeliverydashFragment(partner)
            findNavController().navigate(action)
        } else {
            Toast.makeText(requireContext(), "Invalid Credentials", Toast.LENGTH_SHORT).show()
        }
    }

}