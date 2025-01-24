package com.example.blinkshop.fragments

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.TextSwitcher
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.blinkshop.CartListener
import com.example.blinkshop.Constants
import com.example.blinkshop.R
import com.example.blinkshop.Utils
import com.example.blinkshop.adapters.AdapterBestseller
import com.example.blinkshop.adapters.AdapterProduct
import com.example.blinkshop.adapters.CarouselAdapter
import com.example.blinkshop.adapters.adapterCategory
import com.example.blinkshop.databinding.BsSeeAllBinding
import com.example.blinkshop.databinding.FragmentHomeBinding
import com.example.blinkshop.databinding.ItemViewProductBinding
import com.example.blinkshop.models.Category
import com.example.blinkshop.models.Product
import com.example.blinkshop.models.bestSellers
import com.example.blinkshop.roomdb.CartProducts
import com.example.blinkshop.viewmodels.userViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class HomeFragment : Fragment() {

    private lateinit var rollingTextSwitcher: TextSwitcher
    private val suggestions = listOf(
        "search \"vegetables\"",
        "search \"sweets\"",
        "search \"milk\"",
        "search \"for ata ,dal ,coke\"",
        "search \"chips\"",
        "search \"pooja thali\"",
        "search \"pharma\"",
        "search \"babycare\""
    )
    private var currentIndex = 0

    private lateinit var viewPager: ViewPager2
    private var currentItem = 0
    private val autoScrollDelay = 2500L // 3 seconds delay


    private lateinit var binding: FragmentHomeBinding
    private val viewModel: userViewModel by viewModels()
    private lateinit var adapterBestseller: AdapterBestseller

    private var cartListener: CartListener? = null

    private lateinit var adapterProduct: AdapterProduct
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        playanim()
        setStatusBarColor()
        setSearchSuggestions()
        setViewPager()
        setAllCategories()
        navigatingToSearchFragment()
        get()
        onProfileClicked()
        fetchBestseller()

        return binding.root
    }

    private fun setViewPager() {
        viewPager =binding.viewPager
        val images = listOf(R.drawable.c1, R.drawable.c2, R.drawable.c3, R.drawable.c4, R.drawable.c5)
        val adapter = CarouselAdapter(images)
        viewPager.adapter = adapter

        // Enable auto-swapping
        enableAutoSwapping()
    }

    private fun enableAutoSwapping() {
        lifecycleScope.launch {
            while (true) {
                delay(autoScrollDelay) // Delay between image transitions
                val itemCount = viewPager.adapter?.itemCount ?: 0
                if (itemCount > 0) {
                    currentItem = (currentItem + 1) % itemCount
                    viewPager.setCurrentItem(currentItem, true)
                }
            }
        }
    }
    private fun setSearchSuggestions() {
        rollingTextSwitcher = binding.rollingTextSwitcher

        // Set the factory to dynamically create TextViews with the search icon
        rollingTextSwitcher.setFactory {
            TextView(requireContext()).apply {
                layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    gravity = Gravity.CENTER // Center the text and the icon
                }
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                setTextColor(ContextCompat.getColor(context, R.color.dark_grey))
                hint = "Search"

                // Set the search icon as a compound drawable
                setCompoundDrawablesWithIntrinsicBounds(
                    ContextCompat.getDrawable(context, R.drawable.baseline_search_24), // Left drawable
                    null, // Top drawable
                    null, // Right drawable
                    null  // Bottom drawable
                )
                setPaddingRelative(
                    resources.getDimensionPixelSize(R.dimen._10dp), // paddingStart (left)
                    paddingTop, // default top padding
                    paddingEnd, // default right padding
                    paddingBottom // default bottom padding
                )
                compoundDrawablePadding = resources.getDimensionPixelSize(R.dimen._10dp)
            }
        }

        // Add fade animations for the rolling effect
        rollingTextSwitcher.inAnimation =
            AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in_roll)
        rollingTextSwitcher.outAnimation =
            AnimationUtils.loadAnimation(requireContext(), R.anim.fade_out_roll)

        // Start rolling text suggestions
        startRollingSuggestions()
    }


    private fun startRollingSuggestions() {
        val handler = Handler(Looper.getMainLooper())
        handler.post(object : Runnable {
            override fun run() {
                // Update the TextSwitcher text
                rollingTextSwitcher.setText(suggestions[currentIndex])

                // Move to the next index, looping back to the start
                currentIndex = (currentIndex + 1) % suggestions.size

                // Repeat every 2 seconds
                handler.postDelayed(this, 2000)
            }
        })
    }
    private fun playanim() {
        val lottieAnimation = binding.animationView
        lottieAnimation.playAnimation()
    }

    private fun fetchBestseller() {
        binding.shimmerViewContainer.visibility =View.VISIBLE
        lifecycleScope.launch {
            viewModel.fetchProductTypesforBooksellers().collect{
                adapterBestseller=AdapterBestseller(::onSeeAllButtonClicked)
                binding.rvBestSellers.adapter=adapterBestseller
                adapterBestseller.differ.submitList(it)
                binding.shimmerViewContainer.visibility=View.GONE
            }
        }
    }

    private fun onProfileClicked() {
        binding.ivprofile.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_profileFragment)
        }
    }

    private fun get(){
        lifecycleScope.launch {
        viewModel.getAll().collect {
            for (i in it) {
                Log.d("vvv", i.productTitle.toString())
                Log.d("vvv", i.productCount.toString())
            }
        }
        }
    }
    private fun navigatingToSearchFragment() {
        binding.rollingTextSwitcher.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
        }
    }

    private fun setAllCategories() {
       val categoryList = ArrayList<Category>()
        for(i in 0 until Constants.allProductsCategoryIcon.size){
            categoryList.add(Category(
                Constants.allProductsCategory[i],
                Constants.allProductsCategoryIcon[i]))
        }
        binding.rvCategories.adapter = adapterCategory(categoryList, ::onCategoryIconClicked)
    }
    fun onCategoryIconClicked(category :Category){
        val bundle = Bundle()
        bundle.putString("category",category.title)
        findNavController().navigate(R.id.action_homeFragment_to_categoryFragment,bundle)
    }

    fun onSeeAllButtonClicked(productType : bestSellers){
        val bsSeeAllBinding =BsSeeAllBinding.inflate(LayoutInflater.from(requireContext()))
        val bs=BottomSheetDialog(requireContext())
            bs.setContentView(bsSeeAllBinding.root)

        adapterProduct=AdapterProduct(::onAddButtonClicked,::onIncrementButtonClicked,::onDecrementButtonClicked)
        bsSeeAllBinding.rvProducts.adapter=adapterProduct
        adapterProduct.differ.submitList(productType.products)

        bs.show()
    }




    private fun onAddButtonClicked(product: Product, productBinding: ItemViewProductBinding){
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




    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is CartListener){
            cartListener = context
        }
        else{
            throw ClassCastException("Please implement cartListener")
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
}
// android:pathData="M0,343L24,318.5C48,294 96,245 144,204.2C192,163 240,131 288,171.5C336,212 384,327 432,367.5C480,408 528,376 576,343C624,310 672,278 720,277.7C768,278 816,310 864,334.8C912,359 960,376 1008,343C1056,310 1104,229 1152,179.7C1200,131 1248,114 1296,155.2C1344,196 1392,294 1440,343C1488,392 1536,392 1584,343C1632,294 1680,196 1728,171.5C1776,147 1824,196 1872,179.7C1920,163 1968,82 2016,114.3C2064,147 2112,294 2160,359.3C2208,425 2256,408 2304,375.7C2352,343 2400,294 2448,277.7C2496,261 2544,278 2592,302.2C2640,327 2688,359 2736,326.7C2784,294 2832,196 2880,171.5C2928,147 2976,196 3024,228.7C3072,261 3120,278 3168,261.3C3216,245 3264,196 3312,155.2C3360,114 3408,82 3432,65.3L3456,49L3456,600L3432,600C3408,600 3360,600 3312,600C3264,600 3216,600 3168,600C3120,600 3072,600 3024,600C2976,600 2928,600 2880,600C2832,600 2784,600 2736,600C2688,600 2640,600 2592,600C2544,600 2496,600 2448,600C2400,600 2352,600 2304,600C2256,600 2208,600 2160,600C2112,600 2064,600 2016,600C1968,600 1920,600 1872,600C1824,600 1776,600 1728,600C1680,600 1632,600 1584,600C1536,600 1488,600 1440,600C1392,600 1344,600 1296,600C1248,600 1200,600 1152,600C1104,600 1056,600 1008,600C960,600 912,600 864,600C816,600 768,600 720,600C672,600 624,600 576,600C528,600 480,600 432,600C384,600 336,600 288,600C240,600 192,600 144,600C96,600 48,600 24,600L0,600Z">
