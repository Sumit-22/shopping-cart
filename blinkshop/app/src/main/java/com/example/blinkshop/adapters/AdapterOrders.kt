package com.example.blinkshop.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.blinkshop.R
import com.example.blinkshop.databinding.ItemViewOrdersBinding
import com.example.blinkshop.models.OrderedItems

class AdapterOrders(val context: Context,val onOrderItemViewClicked: (OrderedItems)-> Unit):RecyclerView.Adapter<AdapterOrders.OrdersViewHolder>(){
    class OrdersViewHolder(val binding:ItemViewOrdersBinding ): RecyclerView.ViewHolder(binding.root)


    val diffutil = object : DiffUtil.ItemCallback<OrderedItems>(){
        override fun areItemsTheSame(oldItem: OrderedItems, newItem: OrderedItems): Boolean {
            return oldItem.orderId == newItem.orderId
        }

        override fun areContentsTheSame(oldItem: OrderedItems, newItem: OrderedItems): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this,diffutil)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrdersViewHolder {
        return OrdersViewHolder(ItemViewOrdersBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: OrdersViewHolder, position: Int) {
        val order = differ.currentList[position]
        holder.binding.apply {
            tvOrderTitles.text = order.itemTitle
            tvOrderDate.text=order.itemDate
            tvOrderAmount.text="Rs.${order.itemPrice.toString()}"

            when(order.itemStatus){
                0 ->{
                    tvOrderStatus.text="Ordered"
                    tvOrderStatus.backgroundTintList = ContextCompat.getColorStateList(context , R.color.yellow)
                }1 ->{
                    tvOrderStatus.text="Received"
                    tvOrderStatus.backgroundTintList = ContextCompat.getColorStateList(context, R.color.blue_btn)
                }2 ->{
                    tvOrderStatus.text="Dispatched"
                    tvOrderStatus.backgroundTintList = ContextCompat.getColorStateList(context, R.color.green)
                }3 ->{// in case of inner class we use holder.context while in case of outer class we use context
                tvOrderStatus.text="Delivered"  //holder.context as you know context is nothing but activity.Activity is a specialization of Context. It inherits from the Context class, which means an Activity is also a Context
                tvOrderStatus.backgroundTintList = ContextCompat.getColorStateList(context, R.color.orange)
                }
            }
        }
        holder.itemView.setOnClickListener {  // think about this line
            onOrderItemViewClicked(order)
        }
    }

}











/*
The relationship between Context and Activity in Android is as follows:

Activity is a specialization of Context. It inherits from the Context class, which means an Activity is also a Context.

Context is the main interface to global application functionality. It provides access to resources, databases, shared preferences, etc.
There are two main types of Context in Android:

Application Context: Tied to the lifecycle of the Application. It is a singleton instance that can be accessed via getApplicationContext().

Activity Context: Tied to the lifecycle of an Activity. It is used for the current context. The method of invoking the Activity Context is getContext().
To get the Activity from a Context, you can use a method like getActivity() that recursively unwraps the ContextWrapper until it finds the Activity.
It's important to use the appropriate Context in different situations. Using getApplicationContext() with views can lead to issues, as the view's context should always be the Activity Context.
The best practice is to pass the Activity as a callback to the view, so whenever something happens of interest to the parent, the callback will be fired with the relevant data.*/