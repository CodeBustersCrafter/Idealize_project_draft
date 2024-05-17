package com.codebusters.idealizeprojectdraft.models


data class RequestModel(
    var adId :String = "",
    var buyerId : String = "",
    var sellerId : String ="",
    var isCancelled : String = "0",
    var requestID : String = "" //Me_forWhom_What
)
