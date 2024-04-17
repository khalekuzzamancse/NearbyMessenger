package kzcse.wifidirect.ui.ui.chat_screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import chatui.Conversions

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun ConversationRoute(
    viewModel: ConversionScreenViewModel,
) {
    Conversions(
        conversations = viewModel.messages.collectAsState().value,
        controller = viewModel.messageInputFieldState,
        onSendButtonClick = viewModel::onSendRequest,
        onAttachmentClick = {
        },
        onSpeechToTextRequest = {},
        navigationIcon = null
    )

}
