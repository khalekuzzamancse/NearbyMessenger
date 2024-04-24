package chatui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Attachment
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MessageFieldController {
    private val _message = MutableStateFlow("")
    val message = _message.asStateFlow()
    fun onTextInput(text: String) {
        _message.value = text
    }
    fun clearInputField() {
        _message.value = ""
    }
}


@Composable
internal fun __MessageInputField(
    modifier: Modifier = Modifier,
    controller: MessageFieldController,
    onAttachmentLoadRequest: () -> Unit,
    onSpeechToTextRequest: () -> Unit,
    onSendRequest: () -> Unit
) {
    val emptyMessage = controller.message.collectAsState().value.trim().isEmpty()

    Box(
        modifier = modifier
            .heightIn(min = 60.dp, max = 150.dp)
            .fillMaxWidth()
    ) {
        TextField(
            value = controller.message.collectAsState().value,
            onValueChange = controller::onTextInput,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                focusedIndicatorColor = MaterialTheme.colorScheme.surfaceContainerLow,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.surfaceContainerLow,
            ),
            modifier = Modifier
                .heightIn(min = 60.dp, max = 150.dp)
                .fillMaxWidth()
                .testTag("MessageInputField"),
            placeholder = {
                Text("Type a message", modifier = Modifier.testTag("MessagePlaceholder"))
            }
        )
        if (emptyMessage) {
            Row(
                modifier = Modifier.align(Alignment.BottomEnd)
            ) {
                IconButton(onClick = onAttachmentLoadRequest, modifier = Modifier.testTag("AttachmentButton")) {
                    Icon(
                        imageVector = Icons.Filled.Attachment,
                        contentDescription = "Attach file"
                    )
                }
                IconButton(onClick = onSpeechToTextRequest, modifier = Modifier.testTag("SpeechToTextButton")) {
                    Icon(
                        imageVector = Icons.Filled.Mic,
                        contentDescription = "Activate microphone"
                    )
                }
            }
        } else {
            IconButton(
                onClick = onSendRequest,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .testTag("SendMessageButton")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send message"
                )
            }
        }
    }
}
