package com.example.first_baby.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import com.example.first_baby.models.Product

object Constants {
    const val USERS:String = "users"
    const val VERIFY:String = "verify"
    const val MYSHOPPAL_PRERERENCES :String= "myshoppal_reference"
    const val PRODUCT:String = "product"
    const val BASKET:String = "basket"
    const val ORDER:String = "order"

    const val READ_STORAGE_PERMISSION_CODE = 2
    const val PICK_IMAGE_REQUEST_CODE = 1

    const val EXTRA_PRODUCT_DETAILS:String = "extra_product_details"
    const val EXTRA_PRODUCTS: String = "extra_products"
    const val EXTRA_SHIPPING_INFO:String = "extra_shipping_info"
    const val EXTRA_ORDER_INFO:String = "extra_order_info"

    fun showImageChooser(activity: Activity){
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        activity.startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }

    fun getFileExtension(activity: Activity, uri: Uri?):String? {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }
}