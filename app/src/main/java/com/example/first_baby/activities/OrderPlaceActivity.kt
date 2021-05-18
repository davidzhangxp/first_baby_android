package com.example.first_baby.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.SyncStateContract
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.first_baby.R
import com.example.first_baby.models.Order
import com.example.first_baby.models.Product
import com.example.first_baby.models.Shipping
import com.example.first_baby.utils.Constants
import kotlinx.android.synthetic.main.activity_forgot_password.*
import kotlinx.android.synthetic.main.activity_order_place.*

class OrderPlaceActivity : AppCompatActivity() {
    private lateinit var order:Order
    private lateinit var orderProducts:MutableList<Product>
    private lateinit var shipping: Shipping

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_place)
        if (intent.hasExtra(Constants.EXTRA_ORDER_INFO)){
            order = intent.getParcelableExtra<Order>(Constants.EXTRA_ORDER_INFO)!!
            orderProducts = order.orderItems
        }
        if (intent.hasExtra(Constants.EXTRA_SHIPPING_INFO)){
            shipping = intent.getParcelableExtra<Shipping>(Constants.EXTRA_SHIPPING_INFO)!!
        }
        loadItems()

    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.action_bar_close, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_bar_close -> {
            backToMainActivity()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    private fun loadItems(){
        order_number_label.text = "Order Number is: " + order.orderID
        order_shipping_label.text = "Pickup Address: 19611 Fisher Ave, Poolesville, MD 20837"
        order_total_price.text = "Total Price: " + order.totalPrice
        rv_order_details.apply {
            adapter = PaymentItemListAdapter(orderProducts)
            layoutManager = LinearLayoutManager(this@OrderPlaceActivity)
        }
    }

    private fun backToMainActivity(){
        val intent = Intent(this,MainActivity::class.java)
        startActivity(intent)
    }

}