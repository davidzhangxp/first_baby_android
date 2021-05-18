package com.example.first_baby.utils

import android.content.Context
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.first_baby.R


import java.io.IOException

class GlideLoader(val context: Context) {
    fun loadUserPicture(image:Any,imageView: ImageView){
        try {
            Glide
                .with(context)
                .load(Uri.parse(image.toString()))
                .placeholder(R.drawable.ic_launcher_background)
                .into(imageView)
        }catch (e:IOException){
            e.printStackTrace()
        }
    }
}

