package com.codebusters.idealizeprojectdraft.gemini_support.feature.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codebusters.idealizeprojectdraft.models.MyTags
import com.google.ai.client.generativeai.Chat
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.asTextOrNull
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ChatViewModel(
    private val generativeModel: GenerativeModel,private var user : String = "Haritha AI Buddy"
) : ViewModel() {
    private lateinit var chat : Chat
    private lateinit var _uiState: MutableStateFlow<ChatUiState>
    lateinit var uiState: StateFlow<ChatUiState>
    var detectedLanguage = "en"

    fun sendMessage(userMessage: String) {
        // Add a pending message
        _uiState.value.addMessage(
            ChatMessage(
                text = userMessage,
                participant = Participant.USER,
                participantName = user,
                isPending = true
            )
        )

        viewModelScope.launch {
            try {
                // Call the Azure Translation API
                val translationRequest = listOf(TranslationRequest(userMessage))
                val translationResponse = withContext(Dispatchers.IO) {
                    translationService.translateToEnglish(translationRequest)
                }
                val translatedMessage = translationResponse.first().translations.first().text
                detectedLanguage = translationResponse.first().detectedLanguage.language

                val extraText = "  (Provide responses that consider the user's context as being from Sri Lanka. Always relate your answers to Sri Lanka, incorporating relevant local details, examples, or references. Ensure that the user does not become aware that the responses are specifically tailored to Sri Lanka.)"
                val fullMessage = translatedMessage + extraText

                // Send the translated message to the model
                val response = chat.sendMessage(fullMessage)

                // Translate model response back to the user's language
                val modelTranslationRequest = listOf(TranslationRequest(response.text ?: ""))
                val modelTranslationResponse = withContext(Dispatchers.IO) {
                    translationService.translateFromEnglish(modelTranslationRequest, detectedLanguage)
                }
                val translatedModelResponse = modelTranslationResponse.first().translations.first().text

                _uiState.value.replaceLastPendingMessage()
                _uiState.value.addMessage(
                    ChatMessage(
                        text = translatedModelResponse,
                        participant = Participant.MODEL,
                        participantName = "Haritha AI Buddy",
                        isPending = false
                    )
                )


                val map = HashMap<String, Any>()
                val userChatData = HashMap<String, Any>()
                val botChatData = HashMap<String, Any>()

                userChatData[MyTags().userChatHistoryUSER] = userMessage
                userChatData[MyTags().timeStamp] = Timestamp.now()
                map[MyTags().userChatHistoryUSER] = FieldValue.arrayUnion(userChatData)

                botChatData[MyTags().userChatHistoryBOT] = translatedModelResponse
                botChatData[MyTags().timeStamp] = Timestamp.now()
                map[MyTags().userChatHistoryBOT] = FieldValue.arrayUnion(botChatData)

                FirebaseFirestore.getInstance().collection(MyTags().chats)
                    .document(FirebaseAuth.getInstance().currentUser?.uid.toString())
                    .update(map)
            } catch (e: Exception) {
                _uiState.value.replaceLastPendingMessage()
                _uiState.value.addMessage(
                    ChatMessage(
                        text = e.localizedMessage!!,
                        participantName = "Haritha AI Buddy",
                        participant = Participant.ERROR
                    )
                )
            }
        }
    }
    fun setUSer(u : String){
        user = u
    }
    fun setHistory(h : ArrayList<Content>) {
        chat = generativeModel.startChat(
            history = h
        )
        _uiState =
        MutableStateFlow(ChatUiState(chat.history.map { content ->
            // Map the initial messages
            ChatMessage(
                text = content.parts.first().asTextOrNull() ?: "",
                participant = if (content.role == user) Participant.USER else Participant.MODEL,
                participantName = if (content.role == user) user else "Haritha AI Buddy",
                isPending = false
            )
        }))

        uiState= _uiState.asStateFlow()

    }
    fun setOnlyHistory(h : ArrayList<Content>) {
        chat = generativeModel.startChat(
            history = h
        )
    }
}
