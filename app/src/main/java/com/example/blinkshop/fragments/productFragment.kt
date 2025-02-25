package com.example.blinkshop.fragments

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.denzcoskun.imageslider.models.SlideModel
import com.example.blinkshop.CartListener
import com.example.blinkshop.R
import com.example.blinkshop.Utils
import com.example.blinkshop.adapters.AdapterProduct
import com.example.blinkshop.databinding.FragmentProductBinding
import com.example.blinkshop.databinding.ItemViewProductBinding
import com.example.blinkshop.models.Product
import com.example.blinkshop.roomdb.CartProducts
import com.example.blinkshop.viewmodels.userViewModel
import kotlinx.coroutines.launch


class productFragment : Fragment() {

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        arguments?.let {
//
//        }
//    }

    private lateinit var product: Product

    private lateinit var binding:FragmentProductBinding
    private lateinit var adapterProduct: AdapterProduct
    val viewModel: userViewModel by viewModels()

    private var cartListener : CartListener? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentProductBinding.inflate(layoutInflater)
        fetchProductObject()
        removecolor()
        getAllProduct()
        onplusbtnClicked(product =product )
        onminusButtonclicked(product)
        return binding.root
    }

    private fun onplusbtnClicked(product: Product) {
        binding.btnIncreaseQuantity.setOnClickListener {
            onIncrementButtonClicked(product)
        }
    }

    private fun onIncrementButtonClicked(product: Product) {
        var itemCountInc = binding.tvQuantity.text.toString().toInt()
        itemCountInc++

        if(product.productStock!! + 1 > itemCountInc) {  //why add 1 you know for ex:-stock =3 and qty =1 , since incremented qty is used so we have to add stock by 1 .It's like incremented by 1 on both LHS & RHS.
            binding.tvQuantity.text = itemCountInc.toString()
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

    private fun onminusButtonclicked(product: Product) {
        binding.btnDecreaseQuantity.setOnClickListener {
            onDecrementButtonClicked(product)
        }
    }

    private fun onDecrementButtonClicked(product: Product) {
            var itemCountDec = binding.tvQuantity.text.toString().toInt()
            itemCountDec--
            product.itemCount= itemCountDec  //at first we only use this:-- cartListener?.savingCartItemCount(-1)
            lifecycleScope.launch {
                cartListener?.savingCartItemCount(-1)
                saveProductInDb(product)//BASICALLY IT'S USED TO UPDATING WHOLE INFORMATION OF PRODUCT AGAIN AND AGAIN SO, IN DECREMENT ALSO IT'S NECESSARY TO UPDATE IN ROOM DB
                viewModel.updateItemCount(product,itemCountDec)
            }
            if (itemCountDec > 0){
                binding.tvQuantity.text =  itemCountDec.toString()
            }
            else{
                lifecycleScope.launch {
                    viewModel.deleteCartProduct(product.productRandomId!! )
                }
                Log.d("VV",product.productRandomId!!)
                binding.apply {
                   //tvAdd.visibility = View.VISIBLE
                    //quantitySelector.visibility = View.GONE
                    tvQuantity.text ="0"
                }
            }

            cartListener?.showCartLayout(-1)

        }

    private fun getAllProduct() {
        binding.shimmerView.visibility = View.VISIBLE
        lifecycleScope.launch {
            viewModel.fetchAllProducts().collect {

                if (it.isEmpty()){
                    binding.rvprod.visibility=View.GONE

                }
                else{
                    binding.rvprod.visibility=View.VISIBLE
                }
                adapterProduct = AdapterProduct(
                    ::onProductClicked,
                    ::onAddButtonClicked,
                    ::onIncrementButtonClicked,
                    ::onDecrementButtonClicked
                )
                binding.rvprod.adapter=adapterProduct
                adapterProduct.differ.submitList(it)
                adapterProduct.originalList = it as ArrayList<Product>
                binding.shimmerView.visibility = View.GONE
            }
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


    private fun saveProductInDb(product: Product) {
        val cartProduct = CartProducts(
            productId =product.productRandomId!!,
            productTitle =product.productTitle,
            productQuantity = product.ProductQuantity.toString() +" "+ product.productUnit.toString(),
            productPrice ="Rs."+"${product.productPrice}",
            productCount  =product.itemCount,
            productStock =product.productStock,
            productImage =product. productImageUris?.get(0),//?: "default_image_url"
            productCategory =product.productCategory,
            adminUid =product.adminUid,
            productType =product.productType
        )
        lifecycleScope.launch{
            viewModel.insertCartProduct(cartProduct)

        }
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


    private fun onProductClicked(product: Product,productBinding: ItemViewProductBinding){
        val action = searchFragmentDirections.actionSearchFragmentToProductFragment(product)
        findNavController().navigate(action)
    }

    private fun removecolor() {
        requireActivity().window?.apply {
            val statusBarColors = ContextCompat.getColor(requireContext(), R.color.white)
            statusBarColor = statusBarColors
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }

    private fun fetchProductObject() {
        // product = arguments?.let { productFragmentArgs.fromBundle(it).product }
        product  = productFragmentArgs.fromBundle(requireArguments()).product
        binding.product = product

        product?.productImageUris?.let{ imageUris->
            setupImageSlider(imageUris)

        }
    }

    private fun setupImageSlider(imageUris: List<String?>) {
        val imageList = ArrayList<SlideModel>()
        for(uri in imageUris){
            imageList.add(SlideModel(uri))
        }
        binding.imgProduct.setImageList(imageList)
    }
}