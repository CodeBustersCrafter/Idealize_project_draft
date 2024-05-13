package com.codebusters.idealizeprojectdraft.models

import android.net.Uri

data class ItemModel(
    var name: String = "",
    var price: String = "",
    var date: String = "",
    var time: String = "",
    var description: String = "",
    var quantity: String = "",
    var photo : Uri = Uri.parse(""),
    var category : String = "",
    var visibility : Int = 0,
    var adId : String = "",
    var idealizeUserID : String = "",
    var email : String = "",
    var uid : String = "",
    var adCount : String = "0",
    var profile : Uri = Uri.parse("https://www.google.com/"),
    var location : String = "",
    var phone : String = "",
    var rating : String = "0.0",
    var username : String = ""
)