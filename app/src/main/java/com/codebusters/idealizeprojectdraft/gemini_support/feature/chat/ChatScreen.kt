package com.codebusters.idealizeprojectdraft.gemini_support.feature.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.codebusters.idealizeprojectdraft.R
import com.codebusters.idealizeprojectdraft.gemini_support.GenerativeViewModelFactory
import com.codebusters.idealizeprojectdraft.models.MyTags
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.TextPart
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ChatRoute(
    user : String = "user",
    h : ArrayList<Content> ,
    chatViewModel: ChatViewModel = viewModel<ChatViewModel>(factory = GenerativeViewModelFactory),
    navController: NavController
) {
    chatViewModel.setUSer(user)
    chatViewModel.setHistory(h)
    val chatUiState by chatViewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Chat History") },
                navigationIcon = {
                    Row{

                        IconButton(
                            onClick = {
                                navController.navigate("menu")
                            }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Go Back",
                                tint = MaterialTheme.colorScheme.onPrimary)
                        }

                        IconButton(
                            onClick = {
                                // Implement logic to delete the message
                                val map = HashMap<String,Any>()
                                map[MyTags().userChatHistoryUSER] = ArrayList<Any>()
                                map[MyTags().userChatHistoryBOT] = ArrayList<Any>()
                                FirebaseFirestore.getInstance().collection(MyTags().chats)
                                    .document(FirebaseAuth.getInstance().currentUser?.uid.toString())
                                    .set(map).addOnSuccessListener {
                                        val history = ArrayList<Content>()
                                        history.add(
                                            Content("model",
                                                listOf(TextPart("Great to meet you. What would you like to know?"))
                                            )
                                        )
                                        navController.navigate("menu")

                                    }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete message")
                        }
                    }
                }
                ,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = colorResource(id = R.color.appgreeen),
                    navigationIconContentColor = colorResource(id = R.color.appgreeen)
                )
            )
        },
        bottomBar = {
            MessageInput(
                onSendMessage = { inputText ->
                    chatViewModel.sendMessage(inputText)},
                resetScroll = {
                    coroutineScope.launch {
                        listState.scrollToItem(0)
                    }
                }
            )
        }
        , containerColor = colorResource(id = R.color.white)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            // Messages List
            ChatList(chatUiState.messages, listState)
        }
    }
}

@Composable
fun ChatList(
    chatMessages: List<ChatMessage>,
    listState: LazyListState
) {
    LazyColumn(
        reverseLayout = true,
        state = listState
    ) {
        items(chatMessages.reversed()) { message ->
            ChatBubbleItem(message)
        }
    }
}
@Composable
fun ChatBubbleItem(
    chatMessage: ChatMessage
) {
    val isModelMessage = chatMessage.participant == Participant.MODEL ||
            chatMessage.participant == Participant.ERROR

    val backgroundColor = when (chatMessage.participant) {
        Participant.MODEL -> colorResource(id = R.color.colorSecondary)
        Participant.USER -> colorResource(id = R.color.accentTeal)
        Participant.ERROR -> MaterialTheme.colorScheme.errorContainer
    }

    val fontFamily = when (chatMessage.participant){
        Participant.MODEL -> FontFamily.Monospace
        Participant.USER -> FontFamily.Default
        Participant.ERROR -> FontFamily.Monospace
    }

    @Suppress("DEPRECATION") val fontStyle = when (chatMessage.participant){
        Participant.MODEL -> FontStyle(0)
        Participant.USER -> FontStyle(1)
        Participant.ERROR -> FontStyle(1)
    }

    val bubbleShape = if (isModelMessage) {
        RoundedCornerShape(4.dp, 20.dp, 20.dp, 20.dp)
    } else {
        RoundedCornerShape(20.dp, 4.dp, 20.dp, 20.dp)
    }

    val horizontalAlignment = if (isModelMessage) {
        Alignment.Start
    } else {
        Alignment.End
    }

    Column(
        horizontalAlignment = horizontalAlignment,
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = chatMessage.participantName,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(bottom = 4.dp),
            color = colorResource(id = R.color.black),
            fontFamily = FontFamily.Cursive,
            fontSize = TextUnit(15f,TextUnitType.Sp),
            fontWeight = FontWeight(600)
        )
        Row {
            if (chatMessage.isPending) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(all = 8.dp)
                    ,color = colorResource(id = R.color.colorSecondary)
                )
            }
            BoxWithConstraints {
                Card(
                    colors = CardDefaults.cardColors(containerColor = backgroundColor),
                    shape = bubbleShape,
                    modifier = Modifier.widthIn(0.dp, maxWidth * 0.9f)
                ) {
                    SelectionContainer {
                        Text(
                            text = chatMessage.text,
                            modifier = Modifier.padding(16.dp),
                            color = colorResource(id = R.color.black),
                            fontStyle = fontStyle,
                            fontFamily = fontFamily,
                            fontWeight = FontWeight(600),
                            fontSize = TextUnit(18f, TextUnitType.Sp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MessageInput(
    onSendMessage: (String) -> Unit,
    resetScroll: () -> Unit = {}
) {
    var userMessage by rememberSaveable { mutableStateOf("") }

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardColors(containerColor = colorResource(id = R.color.lightgreyv2),
            contentColor = colorResource(id = R.color.colorPrimary),
            disabledContentColor = colorResource(id = R.color.grey),
            disabledContainerColor = colorResource(id = R.color.colorSecondary))

    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            OutlinedTextField(
                value = userMessage,
                label = { Text(stringResource(R.string.chat_label)
                    , fontSize = TextUnit(18f,TextUnitType.Sp)
                    ,color = colorResource(id = R.color.colorPrimaryDark)) },
                onValueChange = { userMessage = it },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences
                ),
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .fillMaxWidth()
                    .weight(0.85f)
                    .background(color = colorResource(id = R.color.lightgreyv2)),
                colors = TextFieldDefaults.colors().copy(focusedTextColor = colorResource(id = R.color.black),
                    disabledContainerColor = colorResource(id = R.color.lightgreyv2),
                    focusedContainerColor = colorResource(id = R.color.lightgreyv2),
                    unfocusedLabelColor = colorResource(id = R.color.black),
                    unfocusedContainerColor = colorResource(id = R.color.lightgreyv2),
                    cursorColor = colorResource(id = R.color.colorPrimary),
                    focusedIndicatorColor = colorResource(id = R.color.colorPrimary),
                    unfocusedIndicatorColor = colorResource(id = R.color.colorPrimaryDark))
            )
            IconButton(
                onClick = {
                    if (userMessage.isNotBlank()) {
                        onSendMessage(userMessage)
                        userMessage = ""
                        resetScroll()
                    }
                },
                modifier = Modifier
                    .padding(start = 16.dp)
                    .align(Alignment.CenterVertically)
                    .fillMaxWidth()
                    .weight(0.15f)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.Send,
                    contentDescription = stringResource(R.string.action_send),
                    modifier = Modifier
                )
            }
        }
    }
}