package com.example.first_baby.activities

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.first_baby.R
import com.example.first_baby.firebase.FirestoreClass
import com.example.first_baby.models.Basket
import com.example.first_baby.models.Product
import com.example.first_baby.utils.Constants
import com.example.first_baby.utils.GlideLoader
import com.google.firebase.firestore.FirebaseFirestore


class PaymentItemListAdapter(private val products: MutableList<Product>):RecyclerView.Adapter<PaymentItemListAdapter.BasketViewHolder>() {

    inner class BasketViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        fun bind(product: Product, index: Int){
            val title = itemView.findViewById<TextView>(R.id.product_title)
            val description = itemView.findViewById<TextView>(R.id.product_description)
            val price = itemView.findViewById<TextView>(R.id.product_price)
            val imageView = itemView.findViewById<ImageView>(R.id.itemImage)
            val totalprice = product.productPrice * product.productQty
            title.text = product.productName
            description.text = "Qty: " + product.productQty.toString()
            price.text = "Price:$" + totalprice.toString()
            GlideLoader(itemView.context).loadUserPicture(product.productImg, imageView)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BasketViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.payment_items_list, parent, false)
        return BasketViewHolder(v)
    }

    override fun onBindViewHolder(holder: BasketViewHolder, position: Int) {
        holder.bind(products[position], position)

    }

    override fun getItemCount(): Int {
        return products.size
    }


}