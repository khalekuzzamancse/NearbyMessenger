package com.khalekuzzanman.cse.just.peertopeer.ui.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

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

@Preview
@Composable
fun ConversionScreenPreview() {
    ConversionScreen(messages = createDummyConversation())

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversionScreen(
    messages: List<ConversationScreenMessage>
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Nearby Devices", style = MaterialTheme.typography.titleLarge)
                },
                navigationIcon = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(imageVector = Icons.Filled.Menu, contentDescription = null)
                    }
                },

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
                items(messages) { msg ->
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
            MessageInputField()
        }
    }


}