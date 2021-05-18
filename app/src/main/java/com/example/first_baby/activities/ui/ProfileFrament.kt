package com.example.first_baby.activities.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.first_baby.R
import com.example.first_baby.activities.LoginActivity
import com.example.first_baby.activities.MainActivity
import com.example.first_baby.activities.OrderAdapter
import com.example.first_baby.firebase.FirestoreClass
import com.example.first_baby.models.Order
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFrament : Fragment(),View.OnClickListener {


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_profile, container, false)

        return root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        order_history_label.setOnClickListener(this)
        if (FirestoreClass().getCurrentUserId() !== ""){
            FirestoreClass().downloadOrdersFromFirebase(this)
        }else{
            order_history_label.text = "Please click here to login and check your order."
        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.profile_action_bar_menu,menu)
        super.onCreateOptionsMenu(menu, inflater)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id=item.itemId
        when(id){
            R.id.login->{
                val intent = Intent(activity, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                return true
            }
            R.id.logout->{
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(activity, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onClick(v: View?) {
        if (v != null){
            when(v.id){
                R.id.order_history_label->{
                    val intent = Intent(activity, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
            }
        }
    }

    fun orderIsEmpty(){
        order_history_label.text = "No order record."
    }
    fun successLoadOrders(orders:MutableList<Order>){
Log.i("made","what")
        rv_order_history.apply {
            adapter = OrderAdapter(orders)
            layoutManager = LinearLayoutManager(activity)
        }
    }

}