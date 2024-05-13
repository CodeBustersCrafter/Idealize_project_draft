package com.codebusters.idealizeprojectdraft.models

import android.net.Uri

data class IdealizeUser(
    var email : String = "",
    var uid : String = "",
    var adCount : String = "0",
    var profile : Uri = Uri.parse("https://www.google.com/"),
    var location : String = "",
    var phone : String = "",
    var rating : String = "0.0",
    var name : String = ""
    )