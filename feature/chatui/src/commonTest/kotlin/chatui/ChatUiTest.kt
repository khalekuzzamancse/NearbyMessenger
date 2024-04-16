package chatui

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import org.junit.Rule
import kotlin.test.Test

class ChatUiTest {
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun testMessageDisplay() = runComposeUiTest {
        setContent {
            ConversionScreenPreview()  // Using the screen preview to populate the UI
        }
        onNodeWithTag("ConversationsColumn").assertExists()
        onNodeWithText("Some message").assertExists()
        // Check if input field is visible
        onNodeWithTag("MessageInputField").assertExists()
        // Perform a click on the send button and check for updates
        onNodeWithTag("SendButton").performClick()
        waitForIdle()
        onNodeWithText("New message").assertExists()
    }
}
