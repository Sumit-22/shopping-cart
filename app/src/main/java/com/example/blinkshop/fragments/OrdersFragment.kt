package com.example.blinkshop.fragments

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.blinkshop.R
import com.example.blinkshop.adapters.AdapterOrders
import com.example.blinkshop.databinding.FragmentOrdersBinding
import com.example.blinkshop.models.OrderedItems
import com.example.blinkshop.viewmodels.userViewModel
import kotlinx.coroutines.launch


class OrdersFragment : Fragment() {
    private lateinit var binding: FragmentOrdersBinding
    private val viewModel:userViewModel by viewModels()
    private lateinit var adapterOrders: AdapterOrders
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentOrdersBinding.inflate(layoutInflater)
        setStatusBarColor()

        onBackButtonClicked()

        getAllOrders()
        return binding.root
    }

    private fun getAllOrders() {
        binding.shimmerViewContainer.visibility= View.VISIBLE
        lifecycleScope.launch {
            viewModel.getAllOrders().collect{orderList->
                if(orderList.isNotEmpty()){
                    val orderedList  = ArrayList<OrderedItems>()
                    for(order in orderList){
                        val title = StringBuilder()
                        var totalPrice = 0

                        for (products in order.orderList!!){
                            val price = products.productPrice?.substring(4)?.toInt()////format :-Rs. 100 so start from index 4
                            val itemcount = products.productCount!!
                            totalPrice += (price?.times(itemcount))!!
                            title.append("${products.productTitle} ,")
                        }
                        val orderedItem = OrderedItems(
                            orderId = order.orderId,
                            itemDate = order.orderDate,
                            itemStatus = order.orderStatus,
                            itemTitle = title.toString(),
                            itemPrice =totalPrice
                        )
                        orderedList.add(orderedItem)
                    }
                    adapterOrders = AdapterOrders(requireContext() , ::onOrderItemViewClicked)
                    binding.rvOrders.adapter = adapterOrders
                    adapterOrders.differ.submitList(orderedList)
                    binding.shimmerViewContainer.visibility= View.GONE
                }

            }
        }
    }

    private fun onOrderItemViewClicked(orderedItems: OrderedItems){
        val bundle = Bundle()
        bundle.putInt("status",orderedItems.itemStatus!!)
        bundle.putString("orderId",orderedItems.orderId)
        findNavController().navigate(R.id.action_ordersFragment_to_orderDetailsFragment,bundle)
    }


    private fun onBackButtonClicked() {
        binding.tbOrdersFragment.setNavigationOnClickListener {
            findNavController().navigate(R.id.action_ordersFragment_to_profileFragment)
        }
    }

    private fun setStatusBarColor() {
        activity?.window?.apply {
            val statusBarColors = ContextCompat.getColor(requireContext(), R.color.yellow)
            statusBarColor = statusBarColors
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }
}