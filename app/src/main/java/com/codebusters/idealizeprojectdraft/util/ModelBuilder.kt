package com.codebusters.idealizeprojectdraft.util

import android.net.Uri
import com.codebusters.idealizeprojectdraft.models.IdealizeUser
import com.codebusters.idealizeprojectdraft.models.Item
import com.codebusters.idealizeprojectdraft.models.ItemModel
import com.codebusters.idealizeprojectdraft.models.ItemRequestModel
import com.codebusters.idealizeprojectdraft.models.MyTags
import com.codebusters.idealizeprojectdraft.models.RequestModel
import com.google.firebase.firestore.DocumentSnapshot

class ModelBuilder {
    private var myTags = MyTags()

    fun getAdItem(document: DocumentSnapshot, user: DocumentSnapshot): ItemModel {
        return ItemModel(
            document[myTags.adName].toString(),
            document[myTags.adPrice].toString(),
            document[myTags.adDate].toString(),
            document[myTags.adTime].toString(),
            document[myTags.adDescription].toString(),
            document[myTags.adQuantity].toString(),
            Uri.parse(document[myTags.adPhoto].toString()),
            document[myTags.adCategory].toString(),
            document[myTags.adVisibility].toString(),
            document[myTags.adID].toString(),
            document[myTags.adUser].toString(),
            user.get(myTags.userEmail).toString(),
            user.id,
            user.get(myTags.userAdCount).toString(),
            Uri.parse(user.get(myTags.userPhoto).toString()),
            user.get(myTags.userLocation).toString(),
            user.get(myTags.userPhone).toString(),
            user.get(myTags.userRating).toString(),
            user.get(myTags.userName).toString(),
            document.get(myTags.adViewCount).toString().trim().toInt(),
            document.get(myTags.adRequestCount).toString().trim().toInt(),
            document.get(myTags.adRate).toString(),
            user.get(myTags.userRateCount).toString().trim().toInt()
        )
    }
    fun getAdItem(document: DocumentSnapshot, user: IdealizeUser): ItemModel {
        return ItemModel(
            document[myTags.adName].toString(),
            document[myTags.adPrice].toString(),
            document[myTags.adDate].toString(),
            document[myTags.adTime].toString(),
            document[myTags.adDescription].toString(),
            document[myTags.adQuantity].toString(),
            Uri.parse(document[myTags.adPhoto].toString()),
            document[myTags.adCategory].toString(),
            document[myTags.adVisibility].toString(),
            document[myTags.adID].toString(),
            document[myTags.adUser].toString(),
            user.email,
            user.uid,
            user.adCount,
            user.profile,
            user.location,
            user.phone,
            user.rating,
            user.name,
            document.get(myTags.adViewCount).toString().trim().toInt(),
            document.get(myTags.adRequestCount).toString().trim().toInt(),
            document.get(myTags.adRate).toString(),
            user.rateCount
        )
    }

    @Suppress("UNCHECKED_CAST")
    fun getUser(documentSnapshot: DocumentSnapshot): IdealizeUser {
        val idealizeUser = IdealizeUser(
            documentSnapshot.get(myTags.userEmail).toString(),
            documentSnapshot.id,
            documentSnapshot.get(myTags.userAdCount).toString(),
            Uri.parse(documentSnapshot.get(myTags.userPhoto).toString()),
            documentSnapshot.get(myTags.userLocation).toString(),
            documentSnapshot.get(myTags.userPhone).toString(),
            documentSnapshot.get(myTags.userRating).toString(),
            documentSnapshot.get(myTags.userName).toString(),
            documentSnapshot.get(myTags.userRateCount).toString().trim().toInt()
        )
        return idealizeUser
    }

    fun getItemAsMap(ad: Item): HashMap<String, Any> {
        val map = HashMap<String, Any>()
        map[myTags.adName] = ad.name
        map[myTags.adPrice] = ad.price
        map[myTags.adDate] = ad.date
        map[myTags.adTime] = ad.time
        map[myTags.adDescription] = ad.description
        map[myTags.adQuantity] = ad.quantity
        map[myTags.adPhoto] = ad.photo
        map[myTags.adCategory] = ad.category
        map[myTags.adVisibility] = ad.visibility
        map[myTags.adID] = ad.adId
        map[myTags.adUser] = ad.idealizeUserID
        map[myTags.adViewCount] = ad.viewCount
        map[myTags.adRequestCount] = ad.requestCount
        map[myTags.adRate] = ad.rate
        return map
    }

    fun getItemAsMap(ad: ItemModel): HashMap<String, Any> {
        val map = HashMap<String, Any>()
        map[myTags.adName] = ad.name
        map[myTags.adPrice] = ad.price
        map[myTags.adDate] = ad.date
        map[myTags.adTime] = ad.time
        map[myTags.adDescription] = ad.description
        map[myTags.adQuantity] = ad.quantity
        map[myTags.adPhoto] = ad.photo
        map[myTags.adCategory] = ad.category
        map[myTags.adVisibility] = ad.visibility
        map[myTags.adID] = ad.adId
        map[myTags.adUser] = ad.idealizeUserID
        map[myTags.adViewCount] = ad.viewCount
        map[myTags.adRequestCount] = ad.requestCount
        map[myTags.adRate] = ad.rate
        return map
    }

    fun getRequestAsMap(request : RequestModel) : HashMap<String, Any>{
        val map = HashMap<String, Any>()
        map[myTags.requestAdID] = request.adId
        map[myTags.requestSellerID] = request.sellerId
        map[myTags.requestBuyerID] = request.buyerId
        map[myTags.requestIsCancelled] = request.isCancelled
        map[myTags.requestReview] = request.requestReview
        map[myTags.requestID] = request.requestID
        map[myTags.requestReviewSubmit] = request.isReviewDone
        map[myTags.requestReviewRate] = request.reviewRate
        return map
    }
    fun getRequestItem(request :DocumentSnapshot,document: DocumentSnapshot, user: DocumentSnapshot): ItemRequestModel {
        return ItemRequestModel(
            document[myTags.adName].toString(),
            document[myTags.adPrice].toString(),
            document[myTags.adDate].toString(),
            document[myTags.adTime].toString(),
            document[myTags.adDescription].toString(),
            document[myTags.adQuantity].toString(),
            Uri.parse(document[myTags.adPhoto].toString()),
            document[myTags.adCategory].toString(),
            document[myTags.adVisibility].toString(),
            document[myTags.adID].toString(),
            document[myTags.adUser].toString(),
            user.get(myTags.userEmail).toString(),
            user.get(myTags.userAdCount).toString(),
            Uri.parse(user.get(myTags.userPhoto).toString()),
            user.get(myTags.userLocation).toString(),
            user.get(myTags.userPhone).toString(),
            user.get(myTags.userRating).toString(),
            user.get(myTags.userName).toString(),
            request.get(myTags.requestBuyerID).toString(),
            request.get(myTags.requestID).toString(),
            request.get(myTags.requestIsCancelled).toString(),
            request.get(myTags.requestReview).toString(),
            request.get(myTags.requestReviewSubmit).toString(),
            request.get(myTags.requestReviewRate).toString(),
            document.get(myTags.adViewCount).toString().trim().toInt(),
            document.get(myTags.adRequestCount).toString().trim().toInt(),
            document.get(myTags.adRate).toString(),
            user.get(myTags.userRateCount).toString().trim().toInt()
        )
    }
}