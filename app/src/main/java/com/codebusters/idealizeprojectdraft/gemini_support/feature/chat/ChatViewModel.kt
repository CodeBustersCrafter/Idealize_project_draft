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

class ChatViewModel(
    private val generativeModel: GenerativeModel,private var user : String = "#Code_Busters"
) : ViewModel() {
    private lateinit var chat : Chat
    private lateinit var _uiState: MutableStateFlow<ChatUiState>
    lateinit var uiState: StateFlow<ChatUiState>

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
                val response = chat.sendMessage(userMessage)

                _uiState.value.replaceLastPendingMessage()

                response.text?.let { modelResponse ->
                    _uiState.value.addMessage(
                        ChatMessage(
                            text = modelResponse,
                            participant = Participant.MODEL,
                            participantName = "#Code_Busters",
                            isPending = false
                        )
                    )
                }

                val map = HashMap<String, Any>()
                val data = HashMap<String,Any>()

                data[MyTags().userChatHistoryUSER] = userMessage
                data[MyTags().timeStamp] = Timestamp.now()
                map[MyTags().userChatHistoryUSER] = FieldValue.arrayUnion(data)

                val data2 = HashMap<String,Any>()
                data2[MyTags().userChatHistoryBOT] = response.text?:"No response"
                data2[MyTags().timeStamp] = Timestamp.now()
                map[MyTags().userChatHistoryBOT] = FieldValue.arrayUnion(data2)

                FirebaseFirestore.getInstance().collection(MyTags().chats)
                    .document(FirebaseAuth.getInstance().currentUser?.uid.toString())
                    .update(map)
            } catch (e: Exception) {
                _uiState.value.replaceLastPendingMessage()
                _uiState.value.addMessage(
                    ChatMessage(
                        text = e.localizedMessage!!,
                        participantName = "#Code_Busters",
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
                participantName = if (content.role == user) user else "#Code_Busters",
                isPending = false
            )
        }))

        uiState=
        _uiState.asStateFlow()

    }
    fun setOnlyHistory(h : ArrayList<Content>) {

        chat = generativeModel.startChat(
            history = h
        )
    }
}
