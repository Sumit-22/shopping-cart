package com.example.blinkshop.delivery

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil.setContentView
import com.example.blinkshop.R
import com.example.blinkshop.databinding.FragmentDeliveryAuthBinding


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
        val email =binding.emailInput
        val name =
        setAnim()
        return binding.root
        }


}