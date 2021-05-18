package com.example.first_baby.activities

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.first_baby.R
import com.example.first_baby.firebase.FirestoreClass
import com.example.first_baby.models.MUser
import com.example.first_baby.utils.Constants
import com.facebook.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult
import com.google.firebase.auth.FacebookAuthProvider


class LoginActivity : BaseActivity(),View.OnClickListener {
    companion object{
        private const val RC_SIGN_IN = 120
    }
    lateinit var googleSignInClient:GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    lateinit var callbackManager:CallbackManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        forgot_password.setOnClickListener(this)
        login_btn.setOnClickListener(this)
        tv_register_label.setOnClickListener(this)
        google_login_btn.setOnClickListener(this)
        facebook_login_button.setOnClickListener(this)
        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        auth = FirebaseAuth.getInstance()

        FacebookSdk.sdkInitialize(this);
        callbackManager = CallbackManager.Factory.create()

        facebook_login_button.setReadPermissions("email", "public_profile")
        facebook_login_button.registerCallback(callbackManager, object :
            FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                Log.d("facebookLogin", "facebook:onSuccess:$loginResult")
                handleFacebookAccessToken(loginResult.accessToken)
            }

            override fun onCancel() {
                Log.d("facebooklogin", "facebook:onCancel")
            }

            override fun onError(error: FacebookException) {
                Log.d("facebooklogin", "facebook:onError", error)
            }
        })
    }
    override fun onClick(view: View?) {
        if (view != null){
            when (view.id){
                R.id.forgot_password ->{
                    val intent = Intent(this,ForgotPasswordActivity::class.java)
                    startActivity(intent)
                }
                R.id.login_btn -> {
                    loginUser()
                }
                R.id.tv_register_label -> {
                    val intent = Intent(this,RegisterActivity::class.java)
                    startActivity(intent)
                }
                R.id.google_login_btn -> {
                    googleLoginUser()
                }
//                R.id.facebook_login_button -> {
//                    facebookLoginUser()
//                }
            }
        }
    }

    private fun validateLoginDetails():Boolean{
        return when {

            TextUtils.isEmpty(login_email.text.toString()) -> {
                showErrorSnackBar("Please write your email",true)
                false
            }
            TextUtils.isEmpty(login_password.text.toString()) -> {
                showErrorSnackBar("Please write your password",true)
                false
            }
            else -> {
                true
            }
        }
    }

    private fun loginUser(){
        if (validateLoginDetails()){

            showProgressDialog("please wait")

            val email = login_email.text.toString()
            val password = login_password.text.toString()

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password).addOnCompleteListener { task ->
                hideProgressDialog()
                if (task.isSuccessful){
                    val user = task.result!!.user!!
                    if (user.isEmailVerified){
                        if (user.displayName != null){
                            val userInfo = MUser(user.uid,user.displayName,email,false,true)
                            FirestoreClass().downloadCurrentUserFromFirestore(this,userInfo)
                        }else{
                            val userInfo = MUser(user.uid,"",email,false,true)
                            FirestoreClass().downloadCurrentUserFromFirestore(this,userInfo)
                        }

                    }else {
                        val builder = AlertDialog.Builder(this)
                        builder.setTitle("Email Verification")
                        builder.setMessage("You did not verify your email yet,we\'ll send you Authentication email")
                        builder.setPositiveButton(android.R.string.yes) { dialog, which ->
                            user.sendEmailVerification()
                        }
                        builder.show()
                    }
                }else{
                    showErrorSnackBar(task.exception!!.message.toString(),true)

                }
            }
        }
    }

    private fun googleLoginUser(){
        signIn()
    }
    fun userLoggedInSuccess(){
        startActivity(Intent(this,MainActivity::class.java))
        finish()
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val exception = task.exception
            if (task.isSuccessful){
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    val account = task.getResult(ApiException::class.java)!!
                    Log.d("SignInActivity", "firebaseAuthWithGoogle:" + account.id)
                    firebaseAuthWithGoogle(account.idToken!!)
                } catch (e: ApiException) {
                    // Google Sign In failed, update UI appropriately
                    Log.w("signInActivity", "Google sign in failed", e)
                }
            }else{
                Log.w("signInActivity", exception.toString())
            }
        }

    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("googleSignIn", "signInWithCredential:success")
                    val user = auth.currentUser
                    Log.w("googletexterror","getuserinformation")
                    if (user.displayName != null){
                        val userInfo = MUser(user.uid,user.displayName,user.email,false,true)
                        FirestoreClass().downloadCurrentUserFromFirestore(this,userInfo)
                    }else{
                        val userInfo = MUser(user.uid,"",user.email,false,true)
                        FirestoreClass().downloadCurrentUserFromFirestore(this,userInfo)
                    }

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("googleSignIn", "signInWithCredential:failure", task.exception)
                }
            }
    }

    private fun facebookLoginUser(){
        // Initialize Facebook Login button

        facebook_login_button.registerCallback(callbackManager, object :
            FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                Log.d("facebookLogin", "facebook:onSuccess:$loginResult")
                handleFacebookAccessToken(loginResult.accessToken)
            }

            override fun onCancel() {
                Log.d("facebooklogin", "facebook:onCancel")
            }

            override fun onError(error: FacebookException) {
                Log.d("facebooklogin", "facebook:onError", error)
            }
        })

    }
    private fun handleFacebookAccessToken(token: AccessToken) {

        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("fblogin", "signInWithCredential:success")
                    val user = auth.currentUser
                    if (user.displayName != null){
                        val userInfo = MUser(user.uid,user.displayName,user.email,false,true)
                        FirestoreClass().downloadCurrentUserFromFirestore(this,userInfo)
                    }else{
                        val userInfo = MUser(user.uid,"",user.email,false,true)
                        FirestoreClass().downloadCurrentUserFromFirestore(this,userInfo)
                    }
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("facebooklogin", "signInWithCredential:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()

                }
            }
    }
}