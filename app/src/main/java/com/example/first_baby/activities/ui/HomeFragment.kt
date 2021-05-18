package com.example.first_baby.activities.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.first_baby.activities.LoginActivity
import com.example.first_baby.R
import com.example.first_baby.activities.ProductAdapt
import com.example.first_baby.activities.ProductDetailActivity
import com.example.first_baby.firebase.FirestoreClass
import com.example.first_baby.models.Product
import com.example.first_baby.utils.Constants
import com.example.first_baby.utils.GlideLoader
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {

    private var productArray = mutableListOf<Product>()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FirestoreClass().downloadProductFromFirebase(this)
        searchView.setOnQueryTextListener(object :SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchView.clearFocus()
               val results =  productArray.filter{product-> product.productName.contains(query!!)}
                val sectionedProducts:List<RecyclerItem> = results.groupBy { it.category }.flatMap { (category,productsList)->
                    listOf<RecyclerItem>(RecyclerItem.Section(category)) + productsList.map { RecyclerItem.Product(it.productId,it.productName,it.productPrice,it.category,it.description,it.productImg,it.productQty) }
                }
                rv_products.apply {
                    adapter = ProductsAdapter(sectionedProducts)
                    layoutManager = LinearLayoutManager(activity)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val results = productArray.filter{product-> product.productName.contains(newText!!)}
                val sectionedProducts:List<RecyclerItem> = results.groupBy { it.category }.flatMap { (category,productsList)->
                    listOf<RecyclerItem>(RecyclerItem.Section(category)) + productsList.map { RecyclerItem.Product(it.productId,it.productName,it.productPrice,it.category,it.description,it.productImg,it.productQty) }
                }
                rv_products.apply {
                    adapter = ProductsAdapter(sectionedProducts)
                    layoutManager = LinearLayoutManager(activity)
                }
                return false
            }

        })
    }

    fun successLoadProducts(products:MutableList<Product>){
        productArray = products
        val sectionedProducts:List<RecyclerItem> = productArray.groupBy { it.category }.flatMap { (category,productsList)->
            listOf<RecyclerItem>(RecyclerItem.Section(category)) + productsList.map { RecyclerItem.Product(it.productId,it.productName,it.productPrice,it.category,it.description,it.productImg,it.productQty) }
        }
        rv_products.apply {
            adapter = ProductsAdapter(sectionedProducts)
            layoutManager = LinearLayoutManager(activity)
        }
    }
    sealed class RecyclerItem{
        class Product(val productId:String,val productName:String,val productPrice:Double,val category:String,val description:String,val productImg:String,val productQty:Int):RecyclerItem()
        class Section(val title:String):RecyclerItem()
    }

    fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
        return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
    }

     inner class ProductsAdapter(val sectionedProducts: List<RecyclerItem>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        private val TYPE_SECTION = 0
        private val TYPE_FOOD = 1
         inner class ProductViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
             fun bind(item: RecyclerItem.Product){
                 val title = itemView.findViewById<TextView>(R.id.product_title)
                 val description = itemView.findViewById<TextView>(R.id.product_description)
                 val price = itemView.findViewById<TextView>(R.id.product_price)
                 var imageView = itemView.findViewById<ImageView>(R.id.itemImage)
                 title.text = item.productName
                 description.text = item.description
                 price.text = item.productPrice.toString()
                 GlideLoader(itemView.context).loadUserPicture(item.productImg, imageView)

                 itemView.setOnClickListener {
                     val intent = Intent(itemView.context, ProductDetailActivity::class.java)
                     val product = Product(productId = item.productId,productName = item.productName,productPrice = item.productPrice,productImg = item.productImg)
                     intent.putExtra(Constants.EXTRA_PRODUCT_DETAILS, product)
                     itemView.context.startActivity(intent)
                 }
             }
         }
         inner class SectionViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
             fun bind(item: RecyclerItem.Section){
                 val title = itemView.findViewById<TextView>(R.id.section_title)
                 title.text = item.title
             }
         }
         inner class ErrorViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
             fun bind(item: RecyclerItem){
                 val title = itemView.findViewById<TextView>(R.id.section_title)
                 title.text = "error"
             }
         }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when(viewType) {
            TYPE_SECTION -> SectionViewHolder(parent.inflate(R.layout.layout_section))
            TYPE_FOOD -> ProductViewHolder(parent.inflate(R.layout.product_list))
            else -> ErrorViewHolder(parent.inflate(R.layout.layout_section))
        }

        override fun getItemCount() = sectionedProducts.size

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            when(val item = sectionedProducts[holder.adapterPosition]) {
                is RecyclerItem.Product -> (holder as ProductViewHolder).bind(item)
                is RecyclerItem.Section -> (holder as SectionViewHolder).bind(item)
            }
        }

        override fun getItemViewType(position: Int) = when(sectionedProducts[position]) {
            is RecyclerItem.Product -> TYPE_FOOD
            is RecyclerItem.Section -> TYPE_SECTION
        }
    }


}