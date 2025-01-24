package com.example.blinkshop.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.blinkshop.R
import com.example.blinkshop.adapters.AdapterCartProducts
import com.example.blinkshop.databinding.FragmentOrderDetailsBinding
import com.example.blinkshop.viewmodels.userViewModel
import kotlinx.coroutines.launch

class OrderDetailsFragment : Fragment() {

    private lateinit var binding: FragmentOrderDetailsBinding
    private var status:Int = 0
    private var orderId:String = ""
    private val viewModel: userViewModel by viewModels()
    private lateinit var adapterCardProducts: AdapterCartProducts

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentOrderDetailsBinding.inflate(layoutInflater)
        getValues()
        settingStatus()
        lifecycleScope.launch {

            getOrderedProducts()
        }

        onBackButtonClicked()
        return binding.root
    }

    private fun onBackButtonClicked() {
        binding.tbOrderDetailFragment.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_orderDetailsFragment_to_ordersFragment)
        }
    }


    private suspend fun getOrderedProducts() {
        viewModel.getOrderedProducts(orderId).collect{cartList->
            adapterCardProducts = AdapterCartProducts()
            binding.rvProductsItems.adapter=adapterCardProducts
            adapterCardProducts.differ.submitList(cartList)

        }

    }

    private fun settingStatus() {

        val blueColor = ContextCompat.getColorStateList(requireContext(), R.color.blue_btn)

        when(status){
            0 ->{
                binding.iv1.backgroundTintList = blueColor
            }1 ->{
            binding.iv1.backgroundTintList = blueColor
            binding.iv2.backgroundTintList = blueColor
            binding.view1.backgroundTintList = blueColor
        }2 ->{
            binding.iv1.backgroundTintList = blueColor
            binding.iv2.backgroundTintList = blueColor
            binding.view1.backgroundTintList = blueColor
            binding.iv3.backgroundTintList = blueColor
            binding.view2.backgroundTintList = blueColor
        }3 ->{
            binding.iv1.backgroundTintList = blueColor
            binding.iv2.backgroundTintList = blueColor
            binding.view1.backgroundTintList = blueColor
            binding.iv3.backgroundTintList = blueColor
            binding.view2.backgroundTintList = blueColor
            binding.iv4.backgroundTintList = blueColor
            binding.view3.backgroundTintList = blueColor
            }
        }

/**method 2:-

val blueColor = ContextCompat.getColorStateList(requireContext(), R.color.blue_btn)
        val statusToView = mapOf(
            0 to listOf(binding.iv1),
            1 to listOf(binding.iv1,binding.iv2, binding.view1),
            2 to listOf(binding.iv1,binding.iv2, binding.view1,binding.iv3, binding.view2),
            3 to listOf(binding.iv1,binding.iv2, binding.view1,binding.iv3, binding.view2,binding.iv4, binding.view3))

        val viewsToTint = statusToView.getOrDefault(status, emptyList())
        for (view in viewsToTint) {
            view.backgroundTintList = blueColor
        }

method 3 :-
val blueColor = ContextCompat.getColorStateList(requireContext(), R.color.blue_btn)
val viewsToTint = listOf(binding.iv1, binding.iv2, binding.iv3, binding.iv4,binding.view1,binding.view2,binding.view3)
for (i in 0 until status.coerceIn(0, viewsToTint.size)){
    viewsToTint[i].backgroundTintList = blueColor
}
*/


    }

    private fun getValues() {
val bundle = arguments
status = bundle?.getInt("status")!!
orderId = bundle.getString("orderId")!!

}


}