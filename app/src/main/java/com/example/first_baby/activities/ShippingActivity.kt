package com.example.first_baby.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.example.first_baby.R
import com.example.first_baby.models.Shipping
import com.example.first_baby.utils.Constants
import com.facebook.appevents.suggestedevents.ViewOnClickListener
import kotlinx.android.synthetic.main.activity_shipping.*

class ShippingActivity: BaseActivity(),View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shipping)
        val actionBar = supportActionBar
        actionBar!!.title = "Shipping Information"
        actionBar.setDisplayHomeAsUpEnabled(true)
        shipping_btn.setOnClickListener(this)
        shipping_background.setOnClickListener(this)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }


    private fun validateForm():Boolean{
        if (pickup_checkbox.isChecked){
            return true
        }else if (!TextUtils.isEmpty(first_name.text.toString()) && !TextUtils.isEmpty(last_name.text.toString()) && !TextUtils.isEmpty(shipping_address.text.toString())){
            return true
        }else{
            showErrorSnackBar("Please choose pickup", true)
            return false
        }
    }

    override fun onClick(v: View?) {
        if (v != null){
            when(v.id){
                R.id.shipping_btn->{
                    if (validateForm()){
                        val firstName = first_name.text.toString()
                        val lastName = last_name.text.toString()
                        val address = shipping_address.text.toString()
                        val city = shipping_city.text.toString()
                        val postalCode = shipping_postalCode.text.toString()
                        val country = shipping_country.text.toString()
                        if (pickup_checkbox.isChecked){
                            val shipping = Shipping(firstName,lastName,address,city,postalCode,country,true)
                            val intent = Intent(this,PaymentActivity::class.java)
                            intent.putExtra(Constants.EXTRA_SHIPPING_INFO,shipping)
                            startActivity(intent)
                        }else{
                            val shipping = Shipping(firstName,lastName,address,city,postalCode,country,false)
                            val intent = Intent(this,PaymentActivity::class.java)
                            intent.putExtra(Constants.EXTRA_SHIPPING_INFO,shipping)
                            startActivity(intent)
                        }

                    }
                }
                R.id.shipping_background->{
                    v.hideKeyboard()
                }
            }
        }
    }
    private fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

}