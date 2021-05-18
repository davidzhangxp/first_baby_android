package com.example.first_baby.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.SyncStateContract
import android.util.Log
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.first_baby.R
import com.example.first_baby.firebase.FirestoreClass
import com.example.first_baby.models.MUser
import com.example.first_baby.models.Order
import com.example.first_baby.models.Product
import com.example.first_baby.models.Shipping
import com.example.first_baby.utils.Constants
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_payment.*
import kotlinx.android.synthetic.main.fragment_cart.*
import java.lang.Math.round
import java.util.*

class PaymentActivity : AppCompatActivity(), View.OnClickListener  {

    private lateinit var shippingInfo:Shipping
    private lateinit var orderproducts: MutableList<Product>
    var total_price = 0.0
    var shipping_price = 0.0
    var items_price = 0.0
    var tax_price = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        var actionBar = supportActionBar
        actionBar!!.title = "Payment Detail"
        actionBar.setDisplayHomeAsUpEnabled(true)

        if (intent.hasExtra(Constants.EXTRA_SHIPPING_INFO)){
            shippingInfo = intent.getParcelableExtra<Shipping>(Constants.EXTRA_SHIPPING_INFO)!!
            if (shippingInfo.pickup){
                payment_shipping_address.text = "Pick up"
            }else{
                payment_shipping_address.text = "Shiping Address: " + shippingInfo.firstName + " " + shippingInfo.address + " " + shippingInfo.postalCode
            }

        }
        payment_continue_btn.setOnClickListener(this)
        loadBasketItems()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    fun loadBasketItems(){
        FirestoreClass().loadBasketProductsFromFirebase(this)
    }
    fun successLoadProducts(products: MutableList<Product>){
        rv_payment_items.apply {
            adapter = PaymentItemListAdapter(products)
            layoutManager = LinearLayoutManager(this@PaymentActivity)
        }
        updatePriceLabel(products)
        orderproducts = products
    }

    private fun updatePriceLabel(products: MutableList<Product>){

        for (product in products){
            items_price += product.productPrice * product.productQty
        }
        tax_price = ((items_price * 6)/100).toDouble()
        payment_item_price_label.text = "Item Price: $" + items_price.toString()
        payment_tax_price_label.text = "tax Price: $" + tax_price.toString()
        if (shippingInfo.pickup){
            shipping_price = 0.0
            payment_shipping_price_label.text = "Shipping Price: $" + shipping_price.toString()
            total_price = items_price + tax_price + shipping_price
            payment_total_price_label.text = "Total Price: $" + total_price.toString()
        }else{
            if (items_price > 100.0){
                shipping_price = 0.0
                payment_shipping_price_label.text = "Shipping Price: $" + shipping_price.toString()
                total_price = items_price + tax_price + shipping_price
                payment_total_price_label.text = "Total Price: $" + total_price.toString()
            }else{
                shipping_price = 10.0
                payment_shipping_price_label.text = "Shipping Price: $" + shipping_price.toString()
                total_price = items_price + tax_price + shipping_price
                payment_total_price_label.text = "Total Price: $" + total_price.toString()
            }
        }
    }
    override fun onClick(v: View?) {
        if (v != null){
            when(v.id){
                R.id.payment_continue_btn->{
                    placeOrder()
                }
            }
        }
    }
    private fun placeOrder(){

        val sharedPreferences = this.getSharedPreferences(
            Constants.MYSHOPPAL_PRERERENCES,
            Context.MODE_PRIVATE
        )
        var userName = sharedPreferences.getString("name", "")

        val order = Order(
            userId = FirestoreClass().getCurrentUserId(),
            orderItems = orderproducts,
            shipping = shippingInfo,
            itemsPrice = items_price,
            shippingPrice = shipping_price,
            taxPrice = tax_price,
            totalPrice = total_price,
            paid = false,
            orderID = UUID.randomUUID().toString(),
            date = Date(),
            shippingout = false,
            userName = userName.toString()
        )
        FirestoreClass().saveOrderToFirestore(order)
        FirestoreClass().deleteBasketFromFirestore()
        val intent = Intent(this,OrderPlaceActivity::class.java)
        intent.putExtra(Constants.EXTRA_ORDER_INFO,order)
        intent.putExtra(Constants.EXTRA_SHIPPING_INFO,shippingInfo)
        startActivity(intent)
    }
}