package com.codebusters.idealizeprojectdraft

import android.graphics.Bitmap

data class Item(
    var name: String,
    var price: String,
    var location: String,
    var seller: String,
    var rating: String,
    var phone: String,
    var date: String,
    var time: String,
    var description: String,
    var quantity: String,
    var photo : Bitmap
)
