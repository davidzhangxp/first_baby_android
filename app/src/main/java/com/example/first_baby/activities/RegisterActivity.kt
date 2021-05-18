package com.example.first_baby.activities

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.example.first_baby.R
import com.example.first_baby.firebase.FirestoreClass
import com.example.first_baby.models.MUser
import com.example.first_baby.utils.Constants
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : BaseActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        register_btn.setOnClickListener(this)
        tv_login_label.setOnClickListener(this)
        register_background.setOnClickListener(this)
    }



    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.register_btn -> {
                    registerUser()
                }
                R.id.tv_login_label -> {
                    finish()
                }
                R.id.register_background->{
                    v.hideKeyboard()
                }
            }
        }
    }

    private fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }
    private fun registerUser() {
        if (validateRegisterDetails()) {
            showProgressDialog("please wait")

            val email: String = register_email.text.toString()
            val password: String = register_password.text.toString()
            val name: String = register_userName.text.toString()

            //cteate an instance ande create a register a user
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener(
                    OnCompleteListener<AuthResult> { task ->

                        if (task.isSuccessful) {
                            val firebaseUser: FirebaseUser = task.result!!.user!!
                            val user = MUser(firebaseUser.uid, name, email,  false, false)
                            FirestoreClass().registerUser(this, user)

                            firebaseUser!!.sendEmailVerification()
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            showAlertDialog(this,"Verification email sent", "Please check your email and verfy your account")
                                        }
                                    }

                        } else {
                            hideProgressDialog()
                            showErrorSnackBar(task.exception!!.message.toString(), true)
                        }
                    }
            )
        }
    }

    private fun validateRegisterDetails(): Boolean {
        return when {
            TextUtils.isEmpty(register_userName.text.toString()) -> {
                showErrorSnackBar("Please write your name", true)
                false
            }
            TextUtils.isEmpty(register_email.text.toString()) -> {
                showErrorSnackBar("Please write your email", true)
                false
            }
            TextUtils.isEmpty(register_password.text.toString()) -> {
                showErrorSnackBar("Please write your password", true)
                false
            }
            TextUtils.isEmpty(register_repassword.text.toString()) -> {
                showErrorSnackBar("Please write your confirm password", true)
                false
            }
            register_password.text.toString() != register_repassword.text.toString() -> {
                showErrorSnackBar("Please check your password", true)
                false
            }
            else -> {
                true
            }
        }
    }

    fun alertAction(){
        onBackPressed()
    }
}