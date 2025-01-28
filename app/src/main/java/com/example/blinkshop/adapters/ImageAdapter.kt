package com.example.blinkshop.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.blinkshop.databinding.ImageintroBinding

class ImageAdapter(private val imageList: List<Int>) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

// ViewHolder class with ViewBinding
inner class ImageViewHolder(private val binding: ImageintroBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(imageResId: Int) {
        binding.imageView.setImageResource(imageResId)
    }
}

override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
    // Inflate the layout using ViewBinding
    val binding = ImageintroBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    return ImageViewHolder(binding)
}

override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
    // Get the image resource ID using modular arithmetic for infinite scrolling
    val imageResId = imageList[position % imageList.size]
    holder.bind(imageResId)
}

override fun getItemCount(): Int {
    // Return a large number to simulate infinite scrolling
    return Int.MAX_VALUE
}
}

