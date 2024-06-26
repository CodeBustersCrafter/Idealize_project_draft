package com.codebusters.idealizeprojectdraft.models

class MyTags {
    var userMode: Int = 1
    var guestMode: Int = 0
    var userViewMode: Int = 2
    var requestMode: Int = 1
    var myRequestMode: Int = 0

    var intentType: String = "Type"
    var intentUID: String = "ID"
    var intentFragmentRequest: String = "Fragment"
    var intentNotificationID :  String = "Notification ID"
    var intentAddingAdsMode :  String = "MODE"
    var intentUpdatingAdID: String = "UPDATE ID"
    var addMode: String = "ADD"
    var updateMode: String = "UPDATE"

    //Database
    //Users
    var users: String = "Users"
    var userEmail: String = "Email"
    var userAdCount: String = "Ad_count"
    var userLocation: String = "Location"
    var userName: String = "Name"
    var userPhone: String = "Phone"
    var userPhoto: String = "Photo"
    var userRating: String = "Rating"
    var userUID: String = "User ID"
    var userRequests = "Requests"
    var userMyRequests = "My Requests"
    var userRateCount = "Rate Count"

    //Ads
    var ads: String = "Ads"
    var adID: String = "Ad_ID"
    var adCategory: String = "Category"
    var adDate: String = "Date"
    var adDescription: String = "Description"
    var adName: String = "Name"
    var adPhoto: String = "Photo"
    var adPrice: String = "Price"
    var adTime: String = "Time"
    var adVisibility: String = "Visibility"
    var adQuantity: String = "Quantity"
    var keywords: String = "Keywords" // New Keywords list
    var adLocation: String = "Location"
    var adViewCount : String = "Views"
    var adRequestCount : String = "Requests"
    var adRate : String = "Rating"

    var requestAdID : String = "AD_ID"
    var requestID : String = "Request_ID"
    var requestSellerID : String = "Seller_ID"
    var requestBuyerID : String = "Buyer_ID"
    var requestIsCancelled : String = "Is_Cancelled"
    var requestReview : String = "Is_Request_Sent"
    var requestReviewSubmit : String = "Is_Review_Done"
    var requestReviewRate : String = "Rate"

    var adVisible: String = "1"
    var adNotVisible: String = "0"

    //for Ad class
    var adUser: String = "User"

    var appData : String = "App Data"
    var tags : String = "Tags"
    var cities : String = "Cities"

    val apikey : String  = "AIzaSyDHgeax4THkVyLonaykqSw8i7R-R-dlOYg"
    //val apikey : String = "AIzaSyAjXIS8Joz51sYzdCLlkZo3C2qNP5l5MJs"

    val adRequest : String = "Ad Request"

    val areNewRequests : String = "New Requests"

    val chats : String = "Chats"
    val userChatHistoryUSER : String = "Chats User"
    val userChatHistoryBOT : String = "Chats Bot"
    val timeStamp : String = "Timestamp"
}