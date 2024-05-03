package wifi_hotspot_chat_service.chat

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import chatui.conversations.ConversionRoute
import chatui.viewmodel.ChatViewModel
import wifi_hotspot_chat_service.misc.SnackBarDecorator

@Composable
fun ConversationScreen(
    chatViewModel: ChatViewModel,
) {

        ConversionRoute(
            modifier = Modifier,
            viewModel = chatViewModel
        )



}