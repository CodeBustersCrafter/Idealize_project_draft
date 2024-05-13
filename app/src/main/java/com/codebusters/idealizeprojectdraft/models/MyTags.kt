package com.codebusters.idealizeprojectdraft.models

class MyTags {
    var userMode: Int = 1
    var guestMode: Int = 0

    var intentType: String = "Type"
    var intentUID: String = "ID"

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

    var adVisible: Int = 1
    var adNotVisible: Int = 0

    //for Ad class
    var adUser: String = "User"
}