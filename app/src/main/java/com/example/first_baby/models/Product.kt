package com.example.first_baby.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Product (
        val productId:String = "",
        val productName:String = "",
        val productPrice:Double = 0.0,
        val category:String = "",
        val description:String = "",
        val productImg:String = "",
        var productQty:Int = 0,

        ): Parcelable