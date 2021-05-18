package com.example.first_baby.activities

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.first_baby.R
import com.example.first_baby.models.Order
import com.example.first_baby.utils.Constants
import kotlinx.android.synthetic.main.order_list.view.*

class OrderAdapter(private val orders:MutableList<Order>):RecyclerView.Adapter<OrderAdapter.OrderViewHolder>(){
    class OrderViewHolder(itemView: View):RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.order_list,
            parent,
            false
        )
        val holder = OrderViewHolder(view)
        view.setOnClickListener {
            val intent = Intent(parent.context,OrderDetailActivity::class.java)
            intent.putExtra(Constants.EXTRA_ORDER_INFO,orders[holder.adapterPosition])
            parent.context.startActivity(intent)
        }
        return holder
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.itemView.apply {
            order_history_id.text = orders[position].orderID
            order_history_date.text = orders[position].date.toString()
            order_history_total_price.text = "$" + orders[position].totalPrice.toString()
        }
    }

    override fun getItemCount(): Int {
        return orders.size
    }

}