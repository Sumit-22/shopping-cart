package com.example.blinkshop.fragments

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.blinkshop.CartListener
import com.example.blinkshop.R
import com.example.blinkshop.Utils
import com.example.blinkshop.adapters.AdapterProduct
import com.example.blinkshop.databinding.FragmentCategoryBinding
import com.example.blinkshop.databinding.ItemViewProductBinding
import com.example.blinkshop.models.Product
import com.example.blinkshop.roomdb.CartProducts
import com.example.blinkshop.viewmodels.userViewModel
import kotlinx.coroutines.launch


class categoryFragment : Fragment() {
    private lateinit var binding: FragmentCategoryBinding
    private var category :String? = null
    private lateinit var adapterProduct: AdapterProduct
    private val viewModel : userViewModel by viewModels()
    private var cartListener :CartListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCategoryBinding.inflate(layoutInflater)
        getProductCategory()
        onNavigationIconClick()
        setToolBarTitle()
        onSearchMenuClick()
        fetchCategoryProduct()
        setStatusBarColor()
        return binding.root
    }

    private fun onNavigationIconClick() {
        binding.tbSearchFragment.setNavigationOnClickListener{
            findNavController().navigate(R.id.action_categoryFragment_to_homeFragment)
        }
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

    private fun onSearchMenuClick() {
        binding.tbSearchFragment.setOnMenuItemClickListener{MenuItem->
            when(MenuItem.itemId){
                R.id.searchMenu->{
                    findNavController().navigate(R.id.action_categoryFragment_to_searchFragment)
                    true   // similarly like previousserach fragment and search fragment
                }
                else->{ false
                }
            }
        }
    }

    private fun fetchCategoryProduct() {  // ViewModel is running on a coroutine.This allows the ViewModel to be updated asynchronously and ensures that the UI is updated when the data is ready.
        binding.shimmerViewContainer.visibility = View.VISIBLE
        lifecycleScope.launch{
            viewModel.getCategoryProduct(category!!).collect{
                if (it.isEmpty()){
                    binding.rvProducts.visibility=View.GONE
                    binding.tvText.visibility=View.VISIBLE

                }
                else{
                    binding.rvProducts.visibility=View.VISIBLE
                    binding.tvText.visibility=View.GONE
                }
                adapterProduct = AdapterProduct(::onProductClicked,::onAddButtonClicked,::onIncrementButtonClicked,::onDecrementButtonClicked)
                binding.rvProducts.adapter=adapterProduct
                adapterProduct.differ.submitList(it)
                adapterProduct.originalList = it as ArrayList<Product>
                binding.shimmerViewContainer.visibility = View.GONE
            }
        }

    }

    private fun setToolBarTitle() {
        binding.tbSearchFragment.title = category
    }

    private fun getProductCategory() {
        val bundle =arguments
        category = bundle?.getString("category")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is CartListener){
            cartListener = context
        }
        else{
            throw ClassCastException("Please implement cartListener")
        }
    }

    private fun onAddButtonClicked(product: Product ,productBinding: ItemViewProductBinding){
        productBinding.apply {
            tvAdd.visibility= View.GONE //Now tvNumberOfProductCount from activity_users_main.xml and tvProductCount from item_view_product should be same, on clicking tvIncrementButton
            // from item_view_product,tvNumberOfProductCount and tvProductCount should be increased at the realtime and similarly for clicking on tvDecrementButton and remember here we
            // have also take care of total count of all categories (we can see its implementatiom in UserMainActivity) and that count should be updated on firebase
            llProductCount.visibility=View.VISIBLE
            //step 1:-
            var itemCount = productBinding.tvProductCount.text.toString().toInt()
            itemCount++
            productBinding.tvProductCount.text =  itemCount.toString()
            cartListener?.showCartLayout(1)
            //step 2:-
            product.itemCount= itemCount  //at first we only use this:-- cartListener?.savingCartItemCount(1)
            lifecycleScope.launch {
                cartListener?.savingCartItemCount(1)
                saveProductInDb(product)
                viewModel.updateItemCount(product,itemCount)
            }

        }

    }

    private fun saveProductInDb(product: Product) {
        val cartProduct = CartProducts(
            productId =product.productRandomId?: "",
            productTitle =product.productTitle,
            productQuantity = product.ProductQuantity.toString() +" "+ product.productUnit.toString(),
            productPrice ="Rs. ${product.productPrice}",
            productCount  =product.itemCount,
            productStock =product.productStock,
            productImage =product. productImageUris?.get(0)?: "default_image_url",
            productCategory =product.productCategory,
            adminUid =product.adminUid,
            productType=product.productType
        )
        lifecycleScope.launch{
                viewModel.insertCartProduct(cartProduct)

            }
    }

    private fun onIncrementButtonClicked(product: Product, productBinding: ItemViewProductBinding){
        var itemCountInc = productBinding.tvProductCount.text.toString().toInt()
        itemCountInc++

        if(product.productStock!! + 1 > itemCountInc) {  //why add 1 you know for ex:-stock =3 and qty =1 , since incremented qty is used so we have to add stock by 1 .It's like incremented by 1 on both LHS & RHS.
            productBinding.tvProductCount.text = itemCountInc.toString()
            cartListener?.showCartLayout(1)
            //step 2:-
            product.itemCount =
                itemCountInc  //at first we only use this:-- cartListener?.savingCartItemCount(1)
            lifecycleScope.launch {
                cartListener?.savingCartItemCount(1)
                saveProductInDb(product)
                viewModel.updateItemCount(product, itemCountInc)
            }
        }
        else{
            Utils.showToast(requireContext(),"Can't add more products")
        }
    }
    private fun onProductClicked(product: Product,productBinding: ItemViewProductBinding){
        findNavController().navigate(R.id.action_searchFragment_to_productFragment)
    }

    private fun onDecrementButtonClicked(product: Product, productBinding: ItemViewProductBinding){
        var itemCountDec = productBinding.tvProductCount.text.toString().toInt()
        itemCountDec--
        product.itemCount= itemCountDec  //at first we only use this:-- cartListener?.savingCartItemCount(-1)
        lifecycleScope.launch {
            cartListener?.savingCartItemCount(-1)
            saveProductInDb(product)//BASICALLY IT'S USED TO UPDATING WHOLE INFORMATION OF PRODUCT AGAIN AND AGAIN SO, IN DECREMENT ALSO IT'S NECESSARY TO UPDATE IN ROOM DB
            viewModel.updateItemCount(product,itemCountDec)
        }
        if (itemCountDec > 0){
            productBinding.tvProductCount.text =  itemCountDec.toString()
        }
        else{
            lifecycleScope.launch {
                viewModel.deleteCartProduct(product.productRandomId!! )
            }
            Log.d("VV",product.productRandomId!!)
            productBinding.apply {
                tvAdd.visibility = View.VISIBLE
                llProductCount.visibility = View.GONE
                tvProductCount.text ="0"
            }
        }

        cartListener?.showCartLayout(-1)

    }



}