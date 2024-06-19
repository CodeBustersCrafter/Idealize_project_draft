package com.codebusters.idealizeprojectdraft.gemini_support

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.codebusters.idealizeprojectdraft.MainActivity
import com.codebusters.idealizeprojectdraft.gemini_support.feature.chat.ChatRoute
import com.codebusters.idealizeprojectdraft.gemini_support.feature.multimodal.PhotoReasoningRoute
import com.codebusters.idealizeprojectdraft.models.MyTags
import com.codebusters.idealizeprojectdraft.ui.theme.GenerativeAISample
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.TextPart
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class GeminiFragment(private val user : String ="Guest") : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                GenerativeAISample {
                    // A surface container using the 'background' color from the theme
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(color = Color.Green),
                    ) {
                        val navController = rememberNavController()
                        var history = ArrayList<Content>()
                        NavHost(navController = navController, startDestination = "menu") {
                            composable("menu") {
                                (activity as MainActivity).changeCurrentPosition(0)
                                MenuScreen(onItemClicked = { routeId ->
                                    FirebaseFirestore.getInstance().collection(MyTags().chats)
                                        .document(FirebaseAuth.getInstance().currentUser?.uid.toString())
                                        .get().addOnSuccessListener {
                                            history = ArrayList()
                                            if(it[MyTags().userChatHistoryUSER]!=null){
                                                val userChatHistory = it[MyTags().userChatHistoryUSER] as ArrayList<*>
                                                val botChatHistory = it[MyTags().userChatHistoryBOT] as ArrayList<*>
                                                var i = 0
                                                while(i<userChatHistory.size){
                                                    var temp = userChatHistory[i] as HashMap<*, *>
                                                    history.add(
                                                        Content("user",
                                                            listOf(TextPart(temp[MyTags().userChatHistoryUSER].toString()))
                                                        )
                                                    )
                                                    temp = botChatHistory[i] as HashMap<*, *>
                                                    history.add(
                                                        Content("model",
                                                            listOf(TextPart(temp[MyTags().userChatHistoryBOT].toString()))
                                                        )
                                                    )
                                                    i++
                                                }
                                            }else{
                                                val map = HashMap<String,Any>()
                                                map[MyTags().userChatHistoryUSER] = ArrayList<Any>()
                                                map[MyTags().userChatHistoryBOT] = ArrayList<Any>()
                                                FirebaseFirestore.getInstance().collection(MyTags().chats)
                                                    .document(FirebaseAuth.getInstance().currentUser?.uid.toString())
                                                    .set(map)
                                            }
                                            history.add(
                                                Content("model",
                                                    listOf(TextPart("Great to meet you. What would you like to know?"))
                                                )
                                            )
                                            navController.navigate(routeId)
                                        }
                                })

                            }
                            composable("photo_reasoning") {
                                (activity as MainActivity).changeCurrentPosition()
                                PhotoReasoningRoute(user, navController = navController)
                            }
                            composable("chat") {
                                (activity as MainActivity).changeCurrentPosition()
                                ChatRoute(user,history, navController = navController)
                            }
                        }
                    }
                }
            }
        }
    }
}
