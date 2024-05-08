package chatui.conversations

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import chatui.Message
import chatui.message.ChatMessage
import chatui.message.MessageFieldController
import chatui.message.__MessageInputField
import chatui.message.dummyConversation
import chatui.viewmodel.ChatViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun ConversionScreenPreview() {
    var conversations by remember { mutableStateOf(dummyConversation()) }
    val controller = remember { MessageFieldController() }
    val sendMessage: () -> Unit = {
        conversations = conversations + ChatMessage(
            message = controller.message.value,
            timestamp = "now",
            senderName = null
        )
    }
    ConversionBase(
        conversations = conversations,
        controller = controller,
        onSendButtonClick = sendMessage,
        onAttachmentClick = {
        },
        onSpeechToTextRequest = {},
    )
}
@Composable
fun ConversionRoute(
    modifier: Modifier = Modifier,
    viewModel: ChatViewModel,
){
    SnackBarDecorator(viewModel.errorMessage.collectAsState().value) {
        _ConversionRoute(
            modifier = Modifier.padding(it),
            viewModel = viewModel
        )
    }

}

@Composable
fun _ConversionRoute(
    modifier: Modifier = Modifier,
    viewModel: ChatViewModel,
) {
    val scope = rememberCoroutineScope()
    var snackBarMessage by remember { mutableStateOf<String?>(null) }
    val showNotImplementedYet: () -> Unit = {
        scope.launch {
            snackBarMessage = "Not implemented yet..."
            delay(2_000)
            snackBarMessage = null
        }
    }
    _SnackBarDecorator(
        snackBarMessage
    ) { scaffoldPadding ->
        ConversionBase(
            modifier = Modifier.padding(scaffoldPadding),
            conversations = viewModel.conversations.collectAsState(emptyList()).value,
            controller = viewModel.controller,
            onSendButtonClick = {
                CoroutineScope(Dispatchers.Default).launch {
                    viewModel.sendMessage()
                }

            },
            onAttachmentClick = showNotImplementedYet,
            onSpeechToTextRequest = showNotImplementedYet,
        )
    }


}

@Composable
private fun ConversionBase(
    modifier: Modifier = Modifier,
    conversations: List<ChatMessage>,
    controller: MessageFieldController,
    onSendButtonClick: () -> Unit,
    onSpeechToTextRequest: () -> Unit,
    onAttachmentClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .padding(start = 8.dp, end = 8.dp)
            .fillMaxHeight()
            .fillMaxWidth()
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .testTag("ConversationList"),  // Test tag for the message list
            reverseLayout = true
        ) {
            items(conversations.reversed()) { msg ->
                val isSender = msg.senderName == null
                val alignment = if (isSender) Alignment.End else Alignment.Start
                Column(
                    modifier = Modifier.fillMaxWidth()
                        .testTag(if (isSender) "SenderMessage" else "ReceiverMessage")
                ) {
                    Message(
                        modifier = Modifier.testTag("MessageInputBox"),
                        senderName = msg.senderName,
                        message = msg.message,
                        timeStamp = msg.timestamp,
                        shape = if (isSender) RoundedCornerShape(
                            topStart = 8.dp,
                            topEnd = 32.dp,
                            bottomStart = 32.dp,
                            bottomEnd = 32.dp
                        )
                        else RoundedCornerShape(
                            topStart = 8.dp,
                            topEnd = 8.dp,
                            bottomStart = 8.dp,
                            bottomEnd = 32.dp
                        ),
                        alignment = alignment,
                        backgroundColor =
                        if (isSender) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.surfaceVariant
                        //telegram uses white for received message that is equivalent to surface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
        __MessageInputField(
            controller = controller,
            onSendRequest = onSendButtonClick,
            onAttachmentLoadRequest = onAttachmentClick,
            onSpeechToTextRequest = onSpeechToTextRequest,
            modifier = Modifier.testTag("MessageInputBox")
        )
    }
}

@Composable
private fun _SnackBarDecorator(
    message: String?,
    content: @Composable (PaddingValues) -> Unit
) {
    val hostState = remember { SnackbarHostState() }

    LaunchedEffect(message) {
        if (message != null)
            hostState.showSnackbar(message = message, duration = SnackbarDuration.Long)
    }
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState)
        }
    ) { scaffoldPadding ->
        Box(Modifier.padding(scaffoldPadding))
        content(scaffoldPadding)
    }
}

