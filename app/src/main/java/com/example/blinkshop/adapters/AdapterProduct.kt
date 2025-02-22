package com.example.blinkshop.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.denzcoskun.imageslider.models.SlideModel
import com.example.blinkshop.FilteringProduct
import com.example.blinkshop.R
import com.example.blinkshop.databinding.ItemViewProductBinding
import com.example.blinkshop.models.Product


class AdapterProduct(
    val onProductClicked: (Product, ItemViewProductBinding) -> Unit,
    val onAddButtonClicked: (Product, ItemViewProductBinding) -> Unit,
    val onIncrementButtonClicked: (Product, ItemViewProductBinding) -> Unit,
    val onDecrementButtonClicked: (Product, ItemViewProductBinding) -> Unit
) : RecyclerView.Adapter<AdapterProduct.ProductViewHolder>(),
    Filterable  {
    class ProductViewHolder(val binding: ItemViewProductBinding) :ViewHolder(binding.root)


    val diffutil = object : DiffUtil.ItemCallback<Product>() {
            override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
                return oldItem.productRandomId == newItem.productRandomId
            }

            override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
                return oldItem == newItem
            }

        }
        val differ = AsyncListDiffer(this,diffutil)
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
            return ProductViewHolder(ItemViewProductBinding.inflate(LayoutInflater.from(parent.context),parent,false))
        }

        override fun getItemCount(): Int {
            return differ.currentList.size
        }

        override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
            val product = differ.currentList[position]
            holder.binding.apply {
                val imageList =ArrayList<SlideModel>()
                val productImages = product. productImageUris
                Log.d("AdapterProduct", " images ")
                if (productImages != null && productImages.isNotEmpty()) {
                    Log.d("AdapterProduct", "No images ")
                    for (imageUrl in productImages) {  // but why
                        imageList.add(SlideModel(imageUrl))
                        Log.d("AdapterProduct", "Adding image to slider: $imageUrl") // Log the URLs
                    }
                }
                else {
                    // Handle the case where there are no product images
                    imageList.add(SlideModel(R.drawable.default_image_url))
                    Log.d("AdapterProduct", "No images found for product, using default")
                }
                ivImageSlider.setImageList(imageList)

                tvProductTitle.text = product.productTitle
                tvProductTitle.setOnClickListener{
                        onProductClicked(product,this)
                    }

                val quantity = product.ProductQuantity.toString() +" "+ product.productUnit

                tvProductQuantity.text=quantity

                tvProductPrice.text= "Rs."+product.productPrice.toString()
                if( product.itemCount!! > 0){ // WHY WE USE HERE ,It's may be important, HERE THERE IS AN ISSUE CHECK TIMING 6:12:00
                    tvProductCount.text = product.itemCount.toString()  //basically it's the solution of the problem (not updating the product's count if we go back ,so, according to product, assign its itemcount)
                    tvAdd.visibility = View.GONE
                    llProductCount.visibility = View.VISIBLE
                }
                else {
                    tvProductCount.text ="0"
                    tvAdd.visibility = View.VISIBLE
                    llProductCount.visibility = View.GONE
                }
                tvAdd.setOnClickListener {
                    onAddButtonClicked(product,this)
                }
                tvIncrementCount.setOnClickListener {
                    onIncrementButtonClicked(product,this)
                }
                tvDecrementCount.setOnClickListener {
                    onDecrementButtonClicked(product,this)
                }

            }


        }
    val filter:FilteringProduct? = null
    var originalList = ArrayList<Product>()
    override fun getFilter(): Filter {
            if(filter == null) return FilteringProduct(this,originalList)
            return filter
    }

}
/*
DiffUtil plays a crucial role in improving the performance of RecyclerView in Android. Let me elaborate on its importance:

Efficient Data Updates:
When you update data in a RecyclerView, it’s essential to notify the adapter about the changes so that it can update the UI accordingly.
Traditional approaches involve notifying the entire dataset, which can be inefficient and lead to unnecessary UI updates.
DiffUtil efficiently calculates the differences between two lists (old and new) and provides a minimal set of updates needed to transition from the old list to the new list.
By using DiffUtil, you avoid unnecessary view rebinds and improve overall performance.
Reduced UI Flickering:
When items are added, removed, or reordered in a RecyclerView, the UI can flicker due to frequent view updates.
DiffUtil ensures that only the affected items are updated, minimizing flickering and providing a smoother user experience.
Optimized Animations:
When you call notifyDataSetChanged() or similar methods, all visible views are rebound, leading to unnecessary animations.
DiffUtil identifies which items have changed, been added, or removed. You can then apply targeted animations (e.g., slide, fade, or scale) to these specific items.
This results in more meaningful and visually appealing animations.
Complex Data Structures:
If your dataset contains complex data structures (e.g., nested lists, custom objects), manually tracking changes becomes challenging.
DiffUtil handles complex data structures by comparing their contents, not just references.
It works well with custom models and nested lists, making it easier to manage updates.
Adapter Efficiency:
DiffUtil integrates seamlessly with the RecyclerView.Adapter.
By using DiffUtil.Callback, you can override methods to define how items are compared (e.g., based on unique IDs or content equality).
The adapter then applies the calculated differences efficiently.
Automatic Background Threading:
DiffUtil performs its calculations on a background thread by default.
This ensures that UI responsiveness is not affected during the diff calculation.
You don’t need to manage threading manually.
In summary, DiffUtil significantly improves the efficiency, responsiveness, and visual quality of your RecyclerView by intelligently handling data updates. It’s a powerful tool for managing dynamic lists in Android applications. 😊
For more details, you can refer to this GeeksforGeeks article1.,medium.com,schemas.android.com


 */