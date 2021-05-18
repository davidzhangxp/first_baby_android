package com.example.first_baby.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.first_baby.R
import com.example.first_baby.models.Order
import com.example.first_baby.models.Product
import com.example.first_baby.utils.Constants
import kotlinx.android.synthetic.main.activity_order_detail.*

class OrderDetailActivity : AppCompatActivity() {
    private lateinit var order: Order
    private lateinit var orderProducts:MutableList<Product>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_detail)
        if (intent.hasExtra(Constants.EXTRA_ORDER_INFO)){
            order = intent.getParcelableExtra<Order>(Constants.EXTRA_ORDER_INFO)!!
            orderProducts = order.orderItems
        }
        var actionBar = supportActionBar
        actionBar!!.title = "Order Detail"
        actionBar.setDisplayHomeAsUpEnabled(true)

        loadItems()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
    private fun loadItems(){
        rv_order_detail.apply {
            adapter = PaymentItemListAdapter(orderProducts)
            layoutManager = LinearLayoutManager(this@OrderDetailActivity)
        }
    }
}