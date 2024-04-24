package chatui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
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
import chatui.message.ChatMessage
import chatui.message.MessageFieldController
import chatui.message.__MessageInputField
import chatui.message.dummyConversation
import chatui.viewmodel.ChatViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@Composable
fun ConversionScreenPreview() {
    var conversations by remember { mutableStateOf(dummyConversation()) }
    val controller = remember { MessageFieldController() }
    val sendMessage:()->Unit={
        conversations=conversations+ ChatMessage(
            message = controller.message.value,
            timestamp = "now",
            isSender = true
        )
    }
    Conversions(
        conversations = conversations,
        controller = controller,
        onSendButtonClick = sendMessage,
        onAttachmentClick = {
        },
        onSpeechToTextRequest = {},
        navigationIcon = null
    )
}

@Composable
fun ConversionScreen(
    viewModel: ChatViewModel,
) {
    val scope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }
    val showNotImplementedYet:()->Unit={
        scope.launch {
            scope.launch {
                snackBarHostState.currentSnackbarData?.dismiss()
                snackBarHostState.showSnackbar(
                    message = "Not implemented yet...",
                    duration = SnackbarDuration.Short
                )
            }
        }
    }
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState)
        }
    ) { scaffoldPadding ->
        Conversions(
            modifier = Modifier.padding(scaffoldPadding),
            conversations = viewModel.conversations.collectAsState().value,
            controller = viewModel.controller,
            onSendButtonClick = {
                CoroutineScope(Dispatchers.Default).launch {
                    viewModel.sendMessage()
                }

            },
            onAttachmentClick = showNotImplementedYet,
            onSpeechToTextRequest =showNotImplementedYet,
            navigationIcon = null
        )
    }


}
//Internal instead of private to access from ui test module
/**
 * @param navigationIcon Nullable so that in expanded window can be used side by side
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun Conversions(
    modifier: Modifier=Modifier,
    conversations: List<ChatMessage>,
    controller: MessageFieldController,
    onSendButtonClick: () -> Unit,
    onSpeechToTextRequest: () -> Unit,
    onAttachmentClick: () -> Unit,
    navigationIcon: (@Composable () -> Unit)?,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    if (navigationIcon != null) {
                        navigationIcon()
                    }
                },
                title = {
                    Text(text = "Conversation",modifier=Modifier.testTag("ScreenTitle"),)
                }
            )
        }
    ) { scaffoldPadding ->
        Conversions(
            modifier = modifier.padding(scaffoldPadding),
            conversations = conversations,
            controller = controller,
            onSendButtonClick = onSendButtonClick,
            onSpeechToTextRequest = onSpeechToTextRequest,
            onAttachmentClick = onAttachmentClick,
        )
    }


}

@Composable
private fun Conversions(
    modifier: Modifier = Modifier,
    conversations: List<ChatMessage>,
    controller: MessageFieldController,
    onSendButtonClick: () -> Unit,
    onSpeechToTextRequest: () -> Unit,
    onAttachmentClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .padding(start = 8.dp, end = 8.dp)
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
                val alignment = if (msg.isSender) Alignment.End else Alignment.Start
                Column(modifier = Modifier.fillMaxWidth().testTag(if (msg.isSender) "SenderMessage" else "ReceiverMessage")) {
                    MessageInputBox(
                        modifier=Modifier.testTag("MessageInputBox"),
                        message = msg.message,
                        timeStamp = msg.timestamp,
                        shape = if (msg.isSender) RoundedCornerShape(topStart = 8.dp, topEnd = 32.dp, bottomStart = 32.dp, bottomEnd = 32.dp)
                        else RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp, bottomStart = 8.dp, bottomEnd = 32.dp),
                        alignment = alignment,
                        backgroundColor = if (msg.isSender) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.secondaryContainer
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
