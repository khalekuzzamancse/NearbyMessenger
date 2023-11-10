package com.khalekuzzanman.cse.just.peertopeer.ui.ui.chat_screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Attachment
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow


class MessageInputFieldState {
    private val _message = MutableStateFlow("")
    val message = _message.asStateFlow()
    fun onTextInput(text: String) {
        _message.value = text
    }
    fun clear() {
        _message.value = ""
    }
}


@Preview
@Composable
fun MessageInputFieldPreview() {
    val state = remember {
        MessageInputFieldState()
    }
    Column(modifier = Modifier.fillMaxWidth()) {
        MessageInputField(state) {}
    }

}

@Composable
fun MessageInputField(
    state: MessageInputFieldState,
    onSendButtonClick: () -> Unit
) {

    val emptyMessage = state.message.collectAsState().value.trim().isEmpty()

    Box(
        modifier = Modifier
            .heightIn(min = 60.dp, max = 150.dp)
            .fillMaxWidth()
    ) {
        TextField(
            value = state.message.collectAsState().value,
            onValueChange = state::onTextInput,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                focusedIndicatorColor = MaterialTheme.colorScheme.surfaceContainerLow,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.surfaceContainerLow,

                ),
            modifier = Modifier
                .heightIn(min = 60.dp, max = 150.dp)
                .fillMaxWidth()

        )
        if (emptyMessage) {
            Row(
                modifier = Modifier.align(Alignment.BottomEnd)
            ) {
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(
                        imageVector = Icons.Filled.Attachment,
                        contentDescription = null,
                    )
                }
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(
                        imageVector = Icons.Filled.Mic,
                        contentDescription = null,
                    )
                }
            }
        } else {
            IconButton(
                onClick = onSendButtonClick,
                modifier = Modifier.align(Alignment.BottomEnd)
            ) {
                Icon(
                    imageVector = Icons.Filled.Send,
                    contentDescription = null,
                )
            }
        }

    }


}