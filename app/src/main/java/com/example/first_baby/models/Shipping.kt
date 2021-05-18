package com.example.first_baby.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Shipping (
    val firstName:String = "",
    val lastName:String = "",
    val address:String = "",
    val city:String = "",
    val postalCode:String = "",
    val county:String = "",
    val pickup:Boolean = false

    ):Parcelable