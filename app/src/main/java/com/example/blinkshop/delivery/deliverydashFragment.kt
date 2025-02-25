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
import com.example.blinkshop.models.DeliveryPartner


class deliverydashFragment : Fragment() {

    private var _binding: FragmentDeliverydashBinding? = null
    private val binding get() = _binding!!
    //private lateinit var binding: FragmentDeliverydashBinding
    //private lateinit var partner:DeliveryPartner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



    }

    private fun fetchPartnerinfo() {
        val args = deliverydashFragmentArgs.fromBundle(requireArguments())
        binding.partner = args.partner

    }

    private fun clickbtnAvailable() {
        binding.btnAvailable.setOnClickListener {
            loadFragment(AvailableFragment())
            //show the available order
        }
    }

    private fun clickbtnDelivered() {
       binding.btnDelivered.setOnClickListener {
           loadFragment(DeliveredFragment())
           //show the delivered order
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
        _binding = FragmentDeliverydashBinding.inflate(layoutInflater)
        fetchPartnerinfo()
        loadFragment(AvailableFragment())
        clickbtnAvailable()
        clickbtnDelivered()
        return binding.root
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Avoid memory leaks
    }

}