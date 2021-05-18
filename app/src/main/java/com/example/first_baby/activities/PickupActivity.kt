package com.example.first_baby.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.first_baby.R
import com.example.first_baby.models.Shipping
import com.example.first_baby.utils.Constants
import kotlinx.android.synthetic.main.activity_pickup.*
import kotlinx.android.synthetic.main.activity_shipping.*

class PickupActivity : AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pickup)
        val actionBar = supportActionBar
        actionBar!!.title = "Shipping Information"
        actionBar.setDisplayHomeAsUpEnabled(true)

        pickup_btn.setOnClickListener(this)
        pickup_checkbox_box.isChecked = true
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.pickup_btn -> {
                    val shipping = Shipping("", "", "", "", "", "", true)
                    val intent = Intent(this, PaymentActivity::class.java)
                    intent.putExtra(Constants.EXTRA_SHIPPING_INFO, shipping)
                    startActivity(intent)
                }
            }
        }
    }

}