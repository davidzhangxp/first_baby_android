package com.example.first_baby.activities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.first_baby.R
import com.example.first_baby.firebase.FirestoreClass
import com.example.first_baby.models.Product
import com.example.first_baby.utils.Constants
import kotlinx.android.synthetic.main.activity_add_new_product.*
import java.io.IOException
import java.util.*

class AddNewProductActivity : BaseActivity(),View.OnClickListener {
    private var selectedImageFileUri : Uri? = null
    private var productImageURL : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_product)
        val actionBar = supportActionBar
        actionBar!!.title = "Add New Product"
        actionBar.setDisplayHomeAsUpEnabled(true)

        add_product_save_btn.setOnClickListener(this)
        add_product_image.setOnClickListener(this)
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onClick(v: View?) {
        if (v != null){
            when(v.id){
                R.id.add_product_save_btn->{
                    if (selectedImageFileUri != null){
                        FirestoreClass().uploadImageToFirebase(this,selectedImageFileUri)
                    }else{
                        saveProductToFirestore()
                    }

                }
                R.id.add_product_image->{
                    if (ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                        Constants.showImageChooser(this)
                    }else{
                        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),Constants.READ_STORAGE_PERMISSION_CODE)
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE){
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                showErrorSnackBar("this storage permission is granted",false)
                Constants.showImageChooser(this)
            }else{
                Toast.makeText(this,"read permission denied",Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK){
            if (requestCode == Constants.PICK_IMAGE_REQUEST_CODE){
                if (data != null){
                    try {
                        selectedImageFileUri = data.data!!
                        add_product_image.setImageURI(selectedImageFileUri)
                    }catch (e:IOException){
                        e.printStackTrace()
                        Toast.makeText(this,"image uploader failed",Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    fun saveProductToFirestore() =
        if(add_product_title.text.isNotEmpty() && add_product_price.text.isNotEmpty() && add_product_description.text.isNotEmpty() && add_product_category.text.isNotEmpty()){
            var price = add_product_price.text.toString()
            var product = Product(
                productId = UUID.randomUUID().toString(),
                productName = add_product_title.text.toString(),
                productPrice = price.toDouble(),
                description = add_product_description.text.toString(),
                category = add_product_category.text.toString(),
                productImg = productImageURL
            )
                FirestoreClass().saveProductToFirestore(this,product)
        }else{
            showErrorSnackBar("Please fill in all information",true)
        }
    fun productSaveSuccess(){
        onBackPressed()
        showErrorSnackBar("save to firestore",false)
    }

    fun productImageUploadSuccess(imageURL:String){
        productImageURL = imageURL
        saveProductToFirestore()
    }
}