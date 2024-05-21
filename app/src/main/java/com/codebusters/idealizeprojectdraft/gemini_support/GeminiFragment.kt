package com.codebusters.idealizeprojectdraft.gemini_support

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.codebusters.idealizeprojectdraft.gemini_support.feature.chat.ChatRoute
import com.codebusters.idealizeprojectdraft.gemini_support.feature.multimodal.PhotoReasoningRoute
import com.codebusters.idealizeprojectdraft.ui.theme.GenerativeAISample

class GeminiFragment : Fragment() {

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
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        val navController = rememberNavController()

                        NavHost(navController = navController, startDestination = "menu") {
                            composable("menu") {
                                MenuScreen(onItemClicked = { routeId ->
                                    navController.navigate(routeId)
                                })
                            }
                            /*composable("summarize") {
                                SummarizeRoute()
                            }*/
                            composable("photo_reasoning") {
                                PhotoReasoningRoute()
                            }
                            composable("chat") {
                                ChatRoute()
                            }
                        }
                    }
                }
            }
        }
    }
}
