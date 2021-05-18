package com.example.first_baby.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast

import com.example.first_baby.R
import com.example.first_baby.activities.BaseActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_forgot_password.*

class ForgotPasswordActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
        setupActionBar()
        forgot_password_btn.setOnClickListener {
            resetPassword()
        }

    }

    private fun setupActionBar(){
        setSupportActionBar(toolbar_forgot_password_activity)
        val actionBar = supportActionBar
                if (actionBar != null){
                    actionBar.setDisplayHomeAsUpEnabled(true)
                    actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24)
                }
        toolbar_forgot_password_activity.setNavigationOnClickListener { onBackPressed() }
    }

    private fun resetPassword(){
        val email:String = forgot_password_email.text.toString()
        if (email.isEmpty()){
            showErrorSnackBar("please enter your email",true)
        }else{
            showProgressDialog("please wait")
            FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener { task ->
                hideProgressDialog()
                if (task.isSuccessful){
                    Toast.makeText(this,"email sent success",Toast.LENGTH_LONG).show()
                    finish()
                }else{
                    showErrorSnackBar(task.exception!!.message.toString(),true)
                }
            }
        }
    }
}