package com.example.first_baby.activities.ui

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.first_baby.R
import com.example.first_baby.R.*
import com.example.first_baby.activities.LoginActivity
import com.example.first_baby.activities.PickupActivity
import com.example.first_baby.activities.ProductDetailActivity
import com.example.first_baby.activities.ShippingActivity
import com.example.first_baby.firebase.FirestoreClass
import com.example.first_baby.models.Basket
import com.example.first_baby.models.Product
import com.example.first_baby.utils.Constants
import com.example.first_baby.utils.GlideLoader
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_cart.*

class CartFragment : Fragment(),View.OnClickListener {

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(layout.fragment_cart, container, false)

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cart_notification.setOnClickListener(this)
        if (FirestoreClass().getCurrentUserId() == ""){
            cart_notification.text = "Please click here to login and check your cart"
            continue_to_payment.setBackgroundColor(Color.WHITE)
                }else{
            FirestoreClass().downloadBasketProductsFromFirebase(this)
        }
    }
    fun successLoadProducts(products: MutableList<Product>){
        rv_cart_products.apply {
            adapter = BasketAdapter(products)
            layoutManager = LinearLayoutManager(activity)
        }
        continue_to_payment.setOnClickListener {
            val intent = Intent(activity,PickupActivity::class.java)
            startActivity(intent)
        }
        updateTotalPrice(products)
    }
    fun cartIsEmpty(){
        continue_to_payment.setText("Your cart is empty")
        continue_to_payment.setBackgroundColor(R.drawable.green_button_background)
    }
    fun updateTotalPrice(products: MutableList<Product>){
        var totalPrice = 0.00
        for (product in products){
            totalPrice = totalPrice + product.productPrice*product.productQty
        }
        cart_total_price.text = "Total Price : " + totalPrice.toString()
    }

    inner class BasketAdapter(private val products: MutableList<Product>): RecyclerView.Adapter<BasketAdapter.BasketViewHolder>() {

        inner class BasketViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
            fun bind(product: Product, index: Int){
                val title = itemView.findViewById<TextView>(R.id.product_title)
                val description = itemView.findViewById<TextView>(R.id.product_description)
                val price = itemView.findViewById<TextView>(R.id.product_price)
                val imageView = itemView.findViewById<ImageView>(R.id.itemImage)
                val deleteBtn = itemView.findViewById<ImageView>(R.id.basket_delete_btn)
                val totalprice = product.productPrice * product.productQty
                title.text = product.productName
                description.text = "Qty: " + product.productQty.toString()
                price.text = totalprice.toString()
                GlideLoader(itemView.context).loadUserPicture(product.productImg, imageView)

                itemView.setOnClickListener {
                    val intent = Intent(itemView.context, ProductDetailActivity::class.java)
                    intent.putExtra(Constants.EXTRA_PRODUCT_DETAILS, product)
                    itemView.context.startActivity(intent)
                }

                deleteBtn.setOnClickListener {
                    deleteItem(index)
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BasketViewHolder {
            val v = LayoutInflater.from(parent.context).inflate(layout.basket_list, parent, false)
            return BasketViewHolder(v)
        }

        override fun onBindViewHolder(holder: BasketViewHolder, position: Int) {
            holder.bind(products[position], position)

        }

        override fun getItemCount(): Int {
            return products.size
        }
        fun deleteItem(index: Int){
            val deleteItemId = products[index].productId

            products.removeAt(index)
            notifyDataSetChanged()

            itemDeleteInFirestore(deleteItemId)
            updateTotalPrice(products)
        }

        fun itemDeleteInFirestore(productId: String){
            val db = FirebaseFirestore.getInstance()
            db.collection(Constants.BASKET).whereEqualTo("productId", productId).whereEqualTo("userId", FirestoreClass().getCurrentUserId())
                    .get()
                    .addOnSuccessListener { documents ->
                        val baseketId = documents.first()!!.toObject(Basket::class.java)!!.id
                        db.collection(Constants.BASKET).document(baseketId)
                                .delete()
                                .addOnSuccessListener { e ->
                                    Log.e("deleteBasket", "success")
                                }

                    }
                    .addOnFailureListener { e ->
                        Log.e("fail to find basketId", "can't find available item in firestore")
                    }
        }
    }

    override fun onClick(v: View?) {
        if (v != null){
            when(v.id){
                R.id.cart_notification->{
                    startActivity(Intent(activity,LoginActivity::class.java))
                }
            }
        }
    }

}