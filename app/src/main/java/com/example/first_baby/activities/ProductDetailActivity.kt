package com.example.first_baby.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import com.example.first_baby.R
import com.example.first_baby.firebase.FirestoreClass
import com.example.first_baby.models.Basket
import com.example.first_baby.models.Product
import com.example.first_baby.utils.Constants
import com.example.first_baby.utils.GlideLoader
import com.facebook.appevents.suggestedevents.ViewOnClickListener
import kotlinx.android.synthetic.main.activity_product_detail.*
import kotlinx.android.synthetic.main.product_list.view.*
import java.util.*

class ProductDetailActivity : BaseActivity(), View.OnClickListener,AdapterView.OnItemSelectedListener {
    private var spinner:Spinner ? = null
    private var arrayAdapter:ArrayAdapter<String> ? = null
    private var itemList = arrayOf("1","2","3","4","5")

    private lateinit var productDetails: Product
    private lateinit var itemQty:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)

        val actionBar = supportActionBar
        actionBar!!.title = "Details"
        actionBar.setDisplayHomeAsUpEnabled(true)

        if (intent.hasExtra(Constants.EXTRA_PRODUCT_DETAILS)) {
            productDetails = intent.getParcelableExtra<Product>(Constants.EXTRA_PRODUCT_DETAILS)!!
            product_detail_name.text = productDetails.productName.toString()
            product_detail_price.text = productDetails.productPrice.toString()
            product_detail_description.text = productDetails.description
            GlideLoader(this).loadUserPicture(productDetails.productImg, product_detail_image)
        }

        add_to_cart_btn.setOnClickListener(this)


        spinner = findViewById(R.id.spinner)
        arrayAdapter =
            ArrayAdapter(applicationContext,android.R.layout.simple_spinner_item,itemList)
        spinner?.adapter = arrayAdapter
        spinner?.onItemSelectedListener = this
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    fun createNewBasket() {
        try {
            val parsedInt = itemQty.toInt()
            val basket = Basket(UUID.randomUUID().toString(), FirestoreClass().getCurrentUserId(), productDetails.productId, parsedInt)
            FirestoreClass().saveBasketToFirebase(this, basket)
            Log.i("try", "get int")
            showErrorSnackBar("Add to your cart successfully",false)
        } catch (nfe: NumberFormatException) {
            Log.i("no valid int", "error")
            showErrorSnackBar("Please fill in correct quantity",true)
        }
    }

    fun updateBasket(basket: Basket) {
        val basketHashMap = HashMap<String, Any>()
        try {
            val parsedInt = itemQty.toInt()
            basketHashMap["productQty"] = basket.productQty + parsedInt
            FirestoreClass().updateBasketData(this, basketHashMap, basket)
        } catch (nfe: NumberFormatException) {
            showErrorSnackBar("Please fill in correct quantity",true)
        }
    }

    fun basketUpdateSuccess() {
        showErrorSnackBar("Add to your cart successfully",false)
//        startActivity(Intent(this, MainActivity::class.java))
//        finish()
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.add_to_cart_btn -> {
                    if (FirestoreClass().getCurrentUserId() == "") {
                        val intent = Intent(this, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    } else {
                        try {
                            val parsedInt = itemQty.toInt()
                            val basket = Basket(UUID.randomUUID().toString(), FirestoreClass().getCurrentUserId(), productDetails.productId, parsedInt)
                            FirestoreClass().downloadBasketFromFirebase(this, basket)
                        } catch (nfe: NumberFormatException) {
                            Log.i("no valid int", "error")
                            Toast.makeText(this, "please fill in correct quantity", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        itemQty = parent?.getItemAtPosition(position) as String
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

}