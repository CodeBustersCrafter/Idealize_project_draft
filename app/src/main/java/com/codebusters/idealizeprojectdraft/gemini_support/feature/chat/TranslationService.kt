package com.codebusters.idealizeprojectdraft.gemini_support.feature.chat

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface TranslationService {
    @Headers(
        "Ocp-Apim-Subscription-Key: 9ae5c3b7551d4a29ae6eac455c72871c",
        "Ocp-Apim-Subscription-Region: southeastasia",
        "Content-Type: application/json"
    )
    @POST("translate?api-version=3.0&to=en")
    suspend fun translateToEnglish(@Body request: List<TranslationRequest>): List<TranslationResponse>

    @Headers(
        "Ocp-Apim-Subscription-Key: 9ae5c3b7551d4a29ae6eac455c72871c",
        "Ocp-Apim-Subscription-Region: southeastasia",
        "Content-Type: application/json"
    )
    @POST("translate?api-version=3.0")
    suspend fun translateFromEnglish(
        @Body request: List<TranslationRequest>,
        @Query("to") to: String
    ): List<TranslationResponse>
}

val retrofit = Retrofit.Builder()
    .baseUrl("https://api.cognitive.microsofttranslator.com/")
    .addConverterFactory(GsonConverterFactory.create())
    .build()

val translationService = retrofit.create(TranslationService::class.java)

