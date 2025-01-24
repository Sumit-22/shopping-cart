package com.example.blinkshop.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.blinkshop.CartListener
import com.example.blinkshop.R
import com.example.blinkshop.adapters.AdapterCartProducts
import com.example.blinkshop.databinding.ActivityUsersMainBinding
import com.example.blinkshop.databinding.BsCartBinding
import com.example.blinkshop.roomdb.CartProducts
import com.example.blinkshop.viewmodels.userViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch

class UsersMainActivity : AppCompatActivity(),CartListener {
    private lateinit var binding: ActivityUsersMainBinding

    private val viewModel : userViewModel by viewModels<userViewModel>()

    private lateinit var cartProductList :List<CartProducts>

    private lateinit var adapterCartProducts: AdapterCartProducts

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUsersMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getAllCartProducts()
        getTotalItemCountInCart()
        onCartClicked()
        onNextButtonClicked()
    }

    private fun onNextButtonClicked() {
        binding.btnNext.setOnClickListener {
            startActivity(Intent(this,OrderPlaceActivity::class.java))
        }
    }

    override fun getAllCartProducts() {
        lifecycleScope.launch {
            viewModel.getAll().collect {
                cartProductList = it
            }
        }
    }

    override fun hideCartLayout() {
        binding.llCart.visibility = View.GONE
        binding.tvNumberOfProductCount.text = "0"
    }

    private fun onCartClicked(){
        binding.llItemCart.setOnClickListener{
            val bsCartProductsBinding = BsCartBinding.inflate(LayoutInflater.from(this))

            val bs = BottomSheetDialog(this)
            bs.setContentView(bsCartProductsBinding.root)


            bsCartProductsBinding.apply{
                tvNumberOfProductCount.text= binding.tvNumberOfProductCount.text
                btnNext.setOnClickListener {
                    startActivity(Intent(this@UsersMainActivity,OrderPlaceActivity::class.java))
                }
                rvProductsItems.adapter = AdapterCartProducts().apply{
                    differ.submitList(cartProductList)
                }
            }
            bs.show()
        }
    }
    private fun getTotalItemCountInCart() {
        viewModel.fetchTotalCartItemCount().observe(this){
            if(it>0){
                binding.llCart.visibility = View.VISIBLE
                binding.tvNumberOfProductCount.text = it.toString()
            }
            else{
                binding.llCart.visibility = View.GONE
            }
             }
        }

    override fun showCartLayout(itemCount:Int) {//basically this method is responsible for updating the cart count value
        val previousCount = binding.tvNumberOfProductCount.text.toString().toInt()
        val updatedCount = previousCount + itemCount //here we add itemCount to previous updatedCount which is the updatedCount of the count of another category
        if(updatedCount > 0 ){
            binding.llCart.visibility = View.VISIBLE
            binding.tvNumberOfProductCount.text = updatedCount.toString()
        }
        else{
            binding.llCart.visibility = View.GONE
            binding.tvNumberOfProductCount.text = "0"
        }
    } 

    override fun savingCartItemCount(itemCount : Int){
            viewModel.fetchTotalCartItemCount().observe(this){ totalItemCount->
                viewModel.savingCartItemCount( totalItemCount +itemCount)
            }


    }


}


