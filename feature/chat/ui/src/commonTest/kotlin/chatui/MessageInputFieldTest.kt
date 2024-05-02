package chatui

import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.runComposeUiTest
import chatui.message.MessageFieldController
import chatui.message.__MessageInputField
import kotlin.test.Test
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class MessageInputFieldTest {
    private lateinit var textField: SemanticsNodeInteraction
    private lateinit var sendButton: SemanticsNodeInteraction
    private lateinit var attachmentButton: SemanticsNodeInteraction
    private lateinit var speechToTextButton: SemanticsNodeInteraction
    private val controller = MessageFieldController()
    private fun ComposeUiTest.initializeNode() {
        textField = onNodeWithTag("MessageInputField")
        sendButton = onNodeWithTag("SendMessageButton")
        attachmentButton = onNodeWithTag("AttachmentButton")
        speechToTextButton = onNodeWithTag("SpeechToTextButton")

    }

    private fun ComposeUiTest.setUI() {
        setContent {
            __MessageInputField(
                controller = controller,
                onAttachmentLoadRequest = { /* Simulate attachment click */ },
                onSpeechToTextRequest = { /* Simulate speech to text request */ },
                onSendRequest = { /* Simulate send button click */ }
            )
        }
    }

    @Test
    fun messageInputTest() = runComposeUiTest {
        // Setting up the test environment
        initializeNode()
        setUI()
        //Testing
        val message = "Hello, world!"
        textField.assertExists()
        textField.performTextInput(message)
        textField.assertTextEquals(message)
    }

    @Test
    fun buttonsVisibilityTesting() = runComposeUiTest {
        initializeNode()
        setUI()
        //When text is empty the Mic or Add Attachment button will be visible
        attachmentButton.assertExists(); assertTrue(attachmentButton.isDisplayed())
        speechToTextButton.assertExists();assertTrue(speechToTextButton.isDisplayed())
        sendButton.assertDoesNotExist()
        //when message is not empty the send  button will be shown,attachment and mic will be hidden
        textField.performTextInput("Hello")
        attachmentButton.assertDoesNotExist()
        speechToTextButton.assertDoesNotExist()
        sendButton.assertExists();assertTrue(sendButton.isDisplayed())
    }

    @Test
    fun listenerLambdaTest() = runComposeUiTest {
        initializeNode()
        //
        var attachmentClicked = false
        var speechToTextClicked = false
        var sendClicked = false
        setContent {
            __MessageInputField(
                controller = controller,
                onAttachmentLoadRequest = { attachmentClicked = true },
                onSpeechToTextRequest = { speechToTextClicked = true },
                onSendRequest = { sendClicked = true }
            )
        }
        //When text is empty the Mic or Add Attachment button will be visible
        attachmentButton.performClick()
        speechToTextButton.performClick()
        assertTrue{attachmentClicked&&speechToTextClicked&&!sendClicked}
       //When text is not empty only the send button is visible
        textField.performTextInput("Hello")
        sendButton.performClick()
        assertTrue(sendClicked)

    }
}
