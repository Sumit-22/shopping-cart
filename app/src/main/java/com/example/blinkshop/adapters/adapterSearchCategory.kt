package com.example.blinkshop.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.blinkshop.databinding.ItemviewproductsearchcategoryBinding
import com.example.blinkshop.models.Category

class adapterSearchCategory(
    private val categoryList: List<Category>, // List of categories
    private val onCategoryIconClicked: (Category) -> Unit // Callback for item clicks
) : RecyclerView.Adapter<adapterSearchCategory.CategoryViewHolder>() {

    private var selectedPosition: Int = -1 // Tracks the selected item

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemviewproductsearchcategoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(categoryList[position], position)
    }

    override fun getItemCount(): Int = categoryList.size

    inner class CategoryViewHolder(private val binding: ItemviewproductsearchcategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(category: Category, position: Int) {
            binding.apply {
                // Set category data
                ivCategoryImage.setImageResource(category.image)
                tvCategoryTitle.text = category.title

                // Show green indicator only for the selected item
                greenIndicator.visibility =
                    if (position == selectedPosition) View.VISIBLE else View.INVISIBLE

                // Handle click events
                root.setOnClickListener {
                    val previousPosition = selectedPosition
                    selectedPosition = position

                    // Notify changes to update UI
                    notifyItemChanged(previousPosition) // Update the previously selected item
                    notifyItemChanged(position) // Update the newly selected item

                    // Invoke the callback with the selected category
                    onCategoryIconClicked(category)
                }
            }
        }
    }
}
