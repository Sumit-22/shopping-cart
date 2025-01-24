package com.example.blinkshop.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.blinkshop.databinding.ItemviewproductcategoryBinding
import com.example.blinkshop.models.Category

class adapterCategory(
    val categoryList: ArrayList<Category>,
    val onCategoryIconClicked: (Category) -> Unit
) : RecyclerView.Adapter<adapterCategory.CategoryViewHolder>(){
    class CategoryViewHolder(val binding : ItemviewproductcategoryBinding) : ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return CategoryViewHolder(ItemviewproductcategoryBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return  categoryList.size
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
       val category = categoryList[position]
        holder.binding.apply{
            ivCategoryImage.setImageResource(category.image)
            tvCategoryTitle.text = category.title
        }
        holder.itemView.setOnClickListener{
            onCategoryIconClicked(category)
        }
    }

}