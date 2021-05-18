package com.example.first_baby.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
class MUser(
        val objectId:String = "",
        val userName:String = "",
        val email:String = "",
        val admin: Boolean = false,
        val verify:Boolean = false,
): Parcelable