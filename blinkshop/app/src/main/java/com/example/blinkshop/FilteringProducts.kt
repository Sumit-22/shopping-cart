package com.example.blinkshop

import android.widget.Filter
import com.example.blinkshop.adapters.AdapterProduct
import com.example.blinkshop.models.Product
import java.util.Locale


class FilteringProduct(
    val adapterProduct: AdapterProduct,
    val filterproducts :ArrayList<Product>
) : Filter(){ //here we have 4 options of import choose widget option    |_________________>>> it is a class that returns result of a searched result having variables like count and values
    override fun performFiltering(searchingText: CharSequence?): FilterResults {
        val filteredResults = FilterResults()
        if(!searchingText.isNullOrEmpty()){
            val query = searchingText.toString().trim().uppercase(Locale.getDefault()).split(" ")
            val filteredproductList = ArrayList<Product>()
            for(product in filterproducts){
                if(query.any {search->
                    product.productTitle?.uppercase(Locale.getDefault())?.contains(search) == true ||
                    product.productCategory?.uppercase(Locale.getDefault())?.contains(search) == true ||
                    product.productPrice?.toString()?.uppercase(Locale.getDefault())?.contains(search) == true ||
                    product.productType?.uppercase(Locale.getDefault())?.contains(search) == true
                }){
                    filteredproductList.add(product)
                }
            }
            filteredResults.apply {
                count = filteredproductList.size
                values =  filteredproductList
            }
        }
        else{
            filteredResults.apply {
                count = filterproducts.size
                values = filterproducts
            }
        }
        return filteredResults
    }

    override fun publishResults(constraint: CharSequence?, filteredResults: FilterResults?) {
        adapterProduct.differ.submitList(filteredResults?.values as ArrayList<Product>)
    }

}