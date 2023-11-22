package com.khalekuzzanman.cse.just.peertopeer.ui.ui.chat_screen

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.khalekuzzanman.cse.just.peertopeer.data_layer.FetchFileStream
import com.khalekuzzanman.cse.just.peertopeer.data_layer.FileExtensions

data class ConversationScreenMessage(
    val message: String,
    val timestamp: String,
    val isSender: Boolean,
)

fun createDummyConversation(): List<ConversationScreenMessage> {
    val dummyConversation = mutableListOf<ConversationScreenMessage>()

    // Adding some sample messages
    dummyConversation.add(ConversationScreenMessage("Hello there!", "2023-11-03 10:00 AM", true))
    dummyConversation.add(
        ConversationScreenMessage(
            "Hi! How can I help you?",
            "2023-11-03 10:05 AM",
            false
        )
    )
    dummyConversation.add(
        ConversationScreenMessage(
            "I have a question about your product.",
            "2023-11-03 10:10 AM",
            true
        )
    )
    dummyConversation.add(
        ConversationScreenMessage(
            "Sure, feel free to ask.",
            "2023-11-03 10:15 AM",
            false
        )
    )
    dummyConversation.add(
        ConversationScreenMessage(
            "What are the features of your latest product?",
            "2023-11-03 10:20 AM",
            true
        )
    )

    return dummyConversation
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun ConversionScreenPreview(
    viewModel: ConversionScreenViewModel,
    onBackArrowClick: () -> Unit = {}
) {
    LaunchedEffect(Unit) {
        viewModel.onConnectionRequest()
    }
    val tag="ConversionScreenPreview->FetchFileStream :"
    var totalReads= remember { 0 }

//
    var showFilePicker by remember {
        mutableStateOf(true)
    }
    Column {
        ConversionScreen(
            messages = viewModel.messages.collectAsState().value,
            inputFieldState = viewModel.messageInputFieldState,
            onSendButtonClick = viewModel::onSendRequest,
            onBackArrowClick = onBackArrowClick,
            onAttachmentClick = {
                showFilePicker = true
            }
        )
    }

    if (showFilePicker) {
        FetchFileStream(
            onFileSelected = {
                val extension = FileExtensions.getFileExtension(it)
                if (extension != null) {
                    viewModel.sendBytes(byteArrayOf(extension.encodingByte))
                    Log.i(tag,"onFileSelected():$extension")
                }

            },
            onReading = {
                viewModel.sendBytes(it)
                totalReads+=it.size
                Log.i(tag, "onReading():${it.size}")
            },
            onReadingFinished = {
                Log.i(tag, "totalReads=$totalReads")
                viewModel.stopSend()

            }

        )
    }


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversionScreen(
    messages: List<ConversationScreenMessage>,
    inputFieldState: MessageInputFieldState,
    onSendButtonClick: () -> Unit,
    onBackArrowClick: () -> Unit = {},
    onAttachmentClick: () -> Unit = {},
) {


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Conversion", style = MaterialTheme.typography.titleLarge)
                },

                navigationIcon = {
                    IconButton(onClick = onBackArrowClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                actions = {
//                    Text(
//                        text = if (isConnected) "Connected" else "Disconnected",
//                        style = MaterialTheme.typography.titleSmall,
//                        color = if (isConnected) Color.Blue else Color.Red
//                    )

                }

            )
        }
    ) { scaffoldPadding ->
        Column(
            modifier = Modifier
                .padding(scaffoldPadding)
                .padding(start = 8.dp, end = 8.dp)
                .fillMaxWidth()
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                reverseLayout = true
            ) {
                items(messages.reversed()) { msg ->
                    val shape = if (msg.isSender)
                        RoundedCornerShape(
                            topStart = 8.dp,
                            topEnd = 32.dp,
                            bottomStart = 32.dp,
                            bottomEnd = 32.dp
                        )
                    else
                        RoundedCornerShape(
                            topStart = 8.dp,
                            topEnd = 8.dp,
                            bottomStart = 8.dp,
                            bottomEnd = 32.dp
                        )
                    val color = if (msg.isSender)
                        MaterialTheme.colorScheme.tertiary
                    else MaterialTheme.colorScheme.secondaryContainer
                    val alignment = if (msg.isSender) Alignment.End else Alignment.Start
                    //
                    //used a extra column so that sender message align at end
                    //receiver message align at start,
                    //this is the concept of wrapper such as decorator design pattern
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Message(
                            message = msg.message,
                            timeStamp = msg.timestamp,
                            shape = shape,
                            alignment = alignment,
                            backgroundColor = color
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                }
            }
            MessageInputField(
                state = inputFieldState,
                onSendButtonClick = onSendButtonClick,
                onAttachmentClick = onAttachmentClick
            )
        }
    }


}