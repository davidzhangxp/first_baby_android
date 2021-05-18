package com.example.first_baby.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Basket (
        val id:String = "",
        val userId: String = "",
        val productId: String = "",
        val productQty: Int = 1
):Parcelable