package com.khalekuzzanman.cse.just.peertopeer.ui.ui.chat_screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ChatScreen() {
}


@Preview
@Composable
fun MessagePreview() {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxSize()
    ) {
        Message(
            message = "It looks ?",
            timeStamp = "11:20 AM",
            shape = RoundedCornerShape(
                topStart = 8.dp,
                topEnd = 8.dp,
                bottomStart = 8.dp,
                bottomEnd = 16.dp
            ),
            alignment = Alignment.End,
            backgroundColor = MaterialTheme.colorScheme.tertiary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Message(
            message = "It looks like you have",
            timeStamp = "11:20 AM",
            shape = RoundedCornerShape(
                topStart = 8.dp,
                topEnd = 16.dp,
                bottomStart = 16.dp,
                bottomEnd = 16.dp
            ),
            alignment = Alignment.Start,
            backgroundColor = MaterialTheme.colorScheme.secondaryContainer
        )

    }

}

@Composable
fun ColumnScope.Message(
    message: String,
    timeStamp: String,
    shape: Shape,
    alignment: Alignment.Horizontal,
    backgroundColor: Color
) {
    Surface(
        shape = shape,
        modifier = Modifier.align(alignment),
        shadowElevation = 1.dp,
        color = backgroundColor
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
            )

            Text(
                text = timeStamp,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.align(Alignment.End).alpha(0.5f) // Set the alpha value (0.5f for 50% transparency)
            )
        }

    }

}


