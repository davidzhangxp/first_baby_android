package com.example.first_baby.activities

import android.content.Intent
import android.net.Uri
import android.net.Uri.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.first_baby.R
import com.example.first_baby.models.Product
import com.example.first_baby.utils.Constants
import com.example.first_baby.utils.GlideLoader
import kotlinx.android.synthetic.main.product_list.view.*

class ProductAdapt(private val products:MutableList<Product>):RecyclerView.Adapter<ProductAdapt.ProductViewHolder>() {
    class ProductViewHolder(itemView: View):RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.product_list,
            parent,
            false
        )
        val holder = ProductViewHolder(view)
        view.setOnClickListener {
            val intent = Intent(parent.context,ProductDetailActivity::class.java)
            intent.putExtra(Constants.EXTRA_PRODUCT_DETAILS,products[holder.adapterPosition])
            parent.context.startActivity(intent)
        }
        return  holder
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.itemView.apply {
            product_title.text = products[position].productName
            product_price.text = products[position].productPrice.toString()
            product_description.text = products[position].description
            GlideLoader(context).loadUserPicture(products[position].productImg,itemImage)

        }

    }

    override fun getItemCount(): Int {
        return products.size
    }

}