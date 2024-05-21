package com.codebusters.idealizeprojectdraft.models


data class RequestModel(
    var adId :String = "",
    var buyerId : String = "",
    var sellerId : String ="",
    var isCancelled : String = "0",//0-> not yet
    var requestReview : String = "0",//0 -> Not yet
    var isReviewDone : String = "0",// not yet
    var reviewRate : String = "0",
    var requestID : String = "" //Me_forWhom_What
)
