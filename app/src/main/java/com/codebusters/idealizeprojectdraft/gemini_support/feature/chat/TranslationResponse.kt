package com.codebusters.idealizeprojectdraft.gemini_support.feature.chat

data class TranslationResponse(
    val detectedLanguage: DetectedLanguage,
    val translations: List<Translation>
)
