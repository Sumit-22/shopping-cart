package com.example.blinkshop.delivery

import AvailableFragment
import DeliveredFragment
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.blinkshop.R
import com.example.blinkshop.databinding.FragmentDeliverydashBinding


class deliverydashFragment : Fragment() {

    private lateinit var binding: FragmentDeliverydashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadFragment(AvailableFragment())
        clickbtnAvailable()
        clickbtnDelivered()
    }

    private fun clickbtnAvailable() {
        binding.btnAvailable.setOnClickListener {
            loadFragment(AvailableFragment())
        }
    }

    private fun clickbtnDelivered() {
       binding.btnDelivered.setOnClickListener {
           loadFragment(DeliveredFragment())
       }
    }

    private fun loadFragment(fragment: Fragment){
        //supportFragmentManager in case of an activity
        //parentfragmentmanager for fragment
        val transaction=parentFragmentManager.beginTransaction()
       //here binding.fragmentContainer is likely a View and not a FragmentContainerView or valid layout ID.
        transaction.replace(R.id.fragmentContainer, fragment)
        transaction.commit()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDeliverydashBinding.inflate(layoutInflater)
        return binding.root
    }

}