package com.example.first_baby.activities

import android.app.AlertDialog
import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.example.first_baby.R
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.dialog_progress.*

open class BaseActivity : AppCompatActivity() {
    private lateinit var mProgressDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
    }

    fun showErrorSnackBar(message: String, errorMessage: Boolean) {
        var snackBar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
        var snackBarView = snackBar.view
        if (errorMessage) {
            snackBarView.setBackgroundColor(
                    ContextCompat.getColor(
                            this, R.color.colorPrimary
                    )
            )
        } else {
            snackBarView.setBackgroundColor(
                    ContextCompat.getColor(
                            this, R.color.colorSnackBarSuccess
                    )
            )
        }
        snackBar.show()
    }


    fun showProgressDialog(text: String) {
        mProgressDialog = Dialog(this)

        mProgressDialog.setContentView(R.layout.dialog_progress)
        mProgressDialog.tv_progress_text.text = text
        mProgressDialog.setCancelable(false)
        mProgressDialog.setCanceledOnTouchOutside(false)

        mProgressDialog.show()
    }

    fun hideProgressDialog() {

        mProgressDialog.dismiss()
    }

    fun showAlertDialog(activity: AppCompatActivity,title: String, message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton(android.R.string.yes) { dialog, which ->
            when(activity){
                is RegisterActivity->{
                    activity.alertAction()
                }

            }

        }
        builder.setNegativeButton(android.R.string.no) { dialog, which ->

        }
        builder.show()

    }
}