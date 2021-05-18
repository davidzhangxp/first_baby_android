package com.example.first_baby.firebase

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.first_baby.activities.*
import com.example.first_baby.activities.ui.CartFragment
import com.example.first_baby.activities.ui.HomeFragment
import com.example.first_baby.activities.ui.ProfileFrament
import com.example.first_baby.models.Basket
import com.example.first_baby.models.MUser
import com.example.first_baby.models.Order
import com.example.first_baby.models.Product
import com.example.first_baby.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class FirestoreClass {

    private val mFireStore = FirebaseFirestore.getInstance()

//Register and login
    fun getCurrentUserId(): String {
        val currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserId = ""
        if (currentUser != null) {
            currentUserId = currentUser.uid
        }
        return currentUserId
    }

    fun registerUser(activity: RegisterActivity, userInfo: MUser) {
        mFireStore.collection(Constants.USERS).document(userInfo.objectId).set(userInfo, SetOptions.merge())
                .addOnSuccessListener {
                    activity.hideProgressDialog()
                    //save to sharedpreference
                    val sharedPreferences = activity.getSharedPreferences(
                            Constants.MYSHOPPAL_PRERERENCES,
                            Context.MODE_PRIVATE
                    )
                    val editor: SharedPreferences.Editor = sharedPreferences.edit()

                    editor.putString("name", userInfo.userName)
                    editor.putString("email", userInfo.email)
                    editor.putString("_id", userInfo.objectId)
                    editor.putBoolean("verify", false)
                    editor.apply()
                }
                .addOnFailureListener { e ->
                    activity.hideProgressDialog()
                    Log.e(activity.javaClass.simpleName, "Error while registering the user",
                            e
                    )
                }
    }

    fun updateUserProfileData(activity: Activity, userHashMap: HashMap<String, Any>) {
        mFireStore.collection(Constants.USERS).document(getCurrentUserId()).update(userHashMap)
                .addOnSuccessListener {
                    when (activity) {
                        is LoginActivity -> {
                            //save to sharedpreference
                            val sharedPreferences = activity.getSharedPreferences(
                                    Constants.MYSHOPPAL_PRERERENCES,
                                    Context.MODE_PRIVATE
                            )
                            val editor: SharedPreferences.Editor = sharedPreferences.edit()
                            editor.putBoolean("verify", false)
                            editor.apply()
                            activity.showErrorSnackBar("Login successfully", false)
                            activity.userLoggedInSuccess()
                        }
                    }
                }
                .addOnFailureListener { e ->
                    when (activity) {
                        is LoginActivity -> {
                            activity.showErrorSnackBar("Error while user login.", true)
                        }
                    }
                }
    }

    fun downloadCurrentUserFromFirestore(activity: Activity, userInfo: MUser) {
        val sharedPreferences = activity.getSharedPreferences(
                Constants.MYSHOPPAL_PRERERENCES,
                Context.MODE_PRIVATE
        )
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        mFireStore.collection(Constants.USERS).document(userInfo.objectId).get()
                .addOnSuccessListener { document ->
                    val user = document.toObject(MUser::class.java)
                    if (user != null) {
                        editor.putString("name", userInfo.userName)
                        when (activity) {
                            is LoginActivity -> {
                                activity.userLoggedInSuccess()
                            }
                        }
                    } else {
                        mFireStore.collection(Constants.USERS).document(userInfo.objectId).set(userInfo, SetOptions.merge())
                                .addOnSuccessListener {
                                    editor.putString("name", userInfo.userName)
                                    when (activity) {
                                        is LoginActivity -> {
                                            activity.userLoggedInSuccess()
                                        }
                                    }
                                }
                                .addOnFailureListener {
                                    when (activity) {
                                        is LoginActivity -> {
                                            activity.showErrorSnackBar("Error while user update login.", true)
                                        }
                                    }
                                }
                    }

                }
                .addOnFailureListener { }
    }



    //product
    fun saveProductToFirestore(activity: Activity, product: Product) {
        mFireStore.collection(Constants.PRODUCT).document(product.productId).set(product,
                SetOptions.merge())
                .addOnSuccessListener {
                    when (activity) {
                        is AddNewProductActivity -> {
                            activity.productSaveSuccess()
                        }
                    }
                }
                .addOnFailureListener { }
    }

    fun uploadImageToFirebase(activity: Activity, imageFileURI: Uri?) {
        val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
                "product-images/" + System.currentTimeMillis() + "." + Constants.getFileExtension(
                        activity,
                        imageFileURI
                )
        )
        sRef.putFile(imageFileURI!!).addOnSuccessListener { taskSnapshot ->
            Log.i(
                    "Firebase Image URL",
                    taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
            )
            taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener { uri ->
                Log.i("Download image URL", uri.toString())
                when (activity) {

                    is AddNewProductActivity -> {
                        activity.productImageUploadSuccess(uri.toString())
                    }
                }
            }
        }
                .addOnFailureListener { exception ->
                    when (activity) {
                        is AddNewProductActivity -> {

                        }
                    }

                    Log.e(activity.javaClass.simpleName,
                            exception.message,
                            exception)
                }
    }

    fun downloadProductFromFirebase(fragment: Fragment) {
        var products = arrayListOf<Product>()


        mFireStore.collection(Constants.PRODUCT)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val product = document.toObject(Product::class.java)!!
                        products.add(product)

                    }
                    when (fragment) {
                        is HomeFragment -> {
                            fragment.successLoadProducts(products)
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("msg", "download products from firestore failure")

                }
    }

    //basket

    fun saveBasketToFirebase(activity: Activity, basket: Basket) {
        mFireStore.collection(Constants.BASKET).document(basket.id).set(basket,
                SetOptions.merge())
                .addOnSuccessListener {
                    when (activity) {
                        is ProductDetailActivity -> {
                            Toast.makeText(
                                    activity,
                                    "add to basket successfully",
                                    Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Log.e(activity.javaClass.simpleName, "Failed to save to firestore")
                }
    }

    fun downloadBasketFromFirebase(activity: Activity, basket: Basket) {
        var baskets = arrayListOf<Basket>()
        mFireStore.collection(Constants.BASKET).whereEqualTo("userId", basket.userId).whereEqualTo("productId", basket.productId)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val basketdata = document.toObject(Basket::class.java)
                        baskets.add(basketdata)
                    }
                    if (baskets.isEmpty()) {
                        when (activity) {
                            is ProductDetailActivity -> {
                                activity.createNewBasket()
                            }
                        }
                    } else {
                        val basket = baskets.first()
                        when (activity) {
                            is ProductDetailActivity -> {
                                activity.updateBasket(basket)
                            }
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Log.i("fail to download basket", "wooo")
                }
    }

    fun updateBasketData(activity: Activity, basketHashMap: HashMap<String, Any>, basket: Basket) {
        mFireStore.collection(Constants.BASKET).document(basket.id).update(basketHashMap)
                .addOnSuccessListener {
                    when (activity) {
                        is ProductDetailActivity -> {
                        activity.basketUpdateSuccess()
                        }
                    }
                    Log.i("Update basket", "success")
                }
                .addOnFailureListener { e ->

                }
    }

//Cart
    fun downloadBasketProductsFromFirebase(fragment: Fragment){
        val baskets = arrayListOf<Basket>()
        mFireStore.collection(Constants.BASKET).whereEqualTo("userId",getCurrentUserId())
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents){
                        val basket = document.toObject(Basket::class.java)
                        baskets.add(basket)
                    }
                    if (baskets.isEmpty()){
                        when(fragment){
                            is CartFragment->{
                            fragment.cartIsEmpty()
                            }
                        }
                    }else{
                        downloadProducts(fragment,baskets)
                    }

                }
                .addOnFailureListener { e ->
                    Log.e("download basket","error")
                }
    }

    fun downloadProducts(fragment: Fragment,baskets:MutableList<Basket>){
        var products = arrayListOf<Product>()
        var count = 0
        for (basket in baskets){
            mFireStore.collection(Constants.PRODUCT).document(basket.productId)
                    .get()
                    .addOnSuccessListener { document ->
                        var product = document.toObject(Product::class.java)
                        product!!.productQty = basket.productQty
                        count += 1
                        products.add(product!!)
                        if (count == baskets.size){
                            when(fragment){
                                is CartFragment->{
                                    fragment.successLoadProducts(products)
                                }
                            }
                        }

                    }
                    .addOnFailureListener { e->
                        Log.e("download products","error")
                    }
        }

    }
    //payment activity
    fun loadBasketProductsFromFirebase(activity: Activity){
        val baskets = arrayListOf<Basket>()
        mFireStore.collection(Constants.BASKET).whereEqualTo("userId",getCurrentUserId())
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents){
                        val basket = document.toObject(Basket::class.java)
                        baskets.add(basket)
                    }
                    loadProducts(activity,baskets)
                }
                .addOnFailureListener { e ->
                    Log.e("download basket","error")
                }
    }

    fun loadProducts(activity: Activity,baskets:MutableList<Basket>){
        var products = arrayListOf<Product>()
        var count = 0
        for (basket in baskets){
            mFireStore.collection(Constants.PRODUCT).document(basket.productId)
                    .get()
                    .addOnSuccessListener { document ->
                        var product = document.toObject(Product::class.java)
                        product!!.productQty = basket.productQty
                        count += 1
                        products.add(product!!)
                        if (count == baskets.size){
                            when(activity){
                                is PaymentActivity->{
                                    activity.successLoadProducts(products)
                                }
                            }
                        }

                    }
                    .addOnFailureListener { e->
                        Log.e("download products","error")
                    }
        }

    }

    fun saveOrderToFirestore(order: Order){
        mFireStore.collection(Constants.ORDER).document(order.orderID).set(order, SetOptions.merge())
                .addOnSuccessListener {

                }
    }

    fun deleteBasketFromFirestore(){
        mFireStore.collection(Constants.BASKET).whereEqualTo("userId",getCurrentUserId())
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents){
                    val basket = document.toObject(Basket::class.java)
                    mFireStore.collection(Constants.BASKET).document(basket.id).delete()
                }
            }
    }

    //Profile order history
    fun downloadOrdersFromFirebase(fragment: Fragment){
        var orders = mutableListOf<Order>()
        mFireStore.collection(Constants.ORDER).whereEqualTo("userId",getCurrentUserId())
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents){
                    val order = document.toObject(Order::class.java)
                    orders.add(order)
                }
                if (orders.isEmpty()){
                    when(fragment){
                        is ProfileFrament->{
                            fragment.orderIsEmpty()
                        }
                    }
                }else{
                    when(fragment){
                        is ProfileFrament->{
                            fragment.successLoadOrders(orders)
                        }
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e("download order","error")
            }
    }

}