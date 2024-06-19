/*
 * Copyright 2023 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.codebusters.idealizeprojectdraft.gemini_support.feature.multimodal

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codebusters.idealizeprojectdraft.gemini_support.feature.chat.TranslationRequest
import com.codebusters.idealizeprojectdraft.gemini_support.feature.chat.translationService
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PhotoReasoningViewModel(
    private val generativeModel: GenerativeModel,private var user: String? = "Guest",private var uid: String? = ""
) : ViewModel() {

    private val _uiState: MutableStateFlow<PhotoReasoningUiState> =
        MutableStateFlow(PhotoReasoningUiState.Initial)
    val uiState: StateFlow<PhotoReasoningUiState> =
        _uiState.asStateFlow()
    var detectedLanguage = "en"


    fun reason(
        userInput: String,
        selectedImages: List<Bitmap>
    ) {
        _uiState.value = PhotoReasoningUiState.Loading

        viewModelScope.launch {
            try {
                // Translate user input to English
                val translationRequest = listOf(TranslationRequest(userInput))
                val translationResponse = withContext(Dispatchers.IO) {
                    translationService.translateToEnglish(translationRequest)
                }
                val translatedInput = translationResponse.first().translations.first().text
                detectedLanguage = translationResponse.first().detectedLanguage.language

                val prompt = "Look at the image(s), and then answer the following question: $translatedInput"

                val inputContent = content {
                    for (bitmap in selectedImages) {
                        image(bitmap)
                    }
                    text(prompt)
                }

                val outputContentBuilder = StringBuilder()

                generativeModel.generateContentStream(inputContent).collect { response ->
                    outputContentBuilder.append(response.text)

                    // Only update the UI state after the full response is collected
                    val fullResponse = outputContentBuilder.toString()

                    // Translate the full model response back to the detected language
                    val modelTranslationRequest = listOf(TranslationRequest(fullResponse))
                    val modelTranslationResponse = withContext(Dispatchers.IO) {
                        translationService.translateFromEnglish(modelTranslationRequest, detectedLanguage)
                    }
                    val translatedModelResponse = modelTranslationResponse.first().translations.first().text

                    _uiState.value = PhotoReasoningUiState.Success(translatedModelResponse)

                }
            } catch (e: Exception) {
                _uiState.value = PhotoReasoningUiState.Error(e.localizedMessage ?: "")
            }
        }
    }

    fun setUser(u: String) {
        user = u
    }

    fun setUserID(u: String) {
        uid = u
    }
}