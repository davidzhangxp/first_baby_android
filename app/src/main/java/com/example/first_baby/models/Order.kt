package com.example.first_baby.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
class Order(
        val userId: String = "",
        val orderItems: MutableList<Product> = mutableListOf(),
        val shipping: Shipping = Shipping("","","","","","",false),
        val itemsPrice: Double = 0.00,
        val shippingPrice: Double = 0.00,
        val taxPrice: Double = 0.00,
        val totalPrice: Double = 0.00,
        val paid: Boolean = false,
        val orderID: String = "",
        val date: Date = Date(),
        val shippingout : Boolean = false,
        val userName: String = ""
): Parcelable
