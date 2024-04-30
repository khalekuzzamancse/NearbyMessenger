package chatbynearbyapi.chat

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import chatui.conversations.ConversionRoute
import chatui.viewmodel.ChatViewModel

@Composable
fun ConversationScreen(
    chatViewModel: ChatViewModel,
) {

        ConversionRoute(
            modifier = Modifier,
            viewModel = chatViewModel
        )


}