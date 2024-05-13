package com.codebusters.idealizeprojectdraft.models

import android.net.Uri

data class Item(
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
    var idealizeUserID : String = ""
)
