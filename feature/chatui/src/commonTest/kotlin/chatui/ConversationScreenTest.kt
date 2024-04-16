package chatui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.runComposeUiTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class ConversationScreenTest {
    private lateinit var msgTypeFieldBox: SemanticsNodeInteraction
    private lateinit var textField: SemanticsNodeInteraction
    private lateinit var screenTitle: SemanticsNodeInteraction
    private lateinit var conversationList: SemanticsNodeInteraction
    private lateinit var sendButton: SemanticsNodeInteraction
    private lateinit var attachmentButton: SemanticsNodeInteraction
    private lateinit var speechToTextButton: SemanticsNodeInteraction

    private var conversations by mutableStateOf(dummyConversation())
    private val controller = MessageFieldController()

    @Test
    fun sendMessageAddedToConversationList() = runComposeUiTest {
        initializeNode()
        val totalConversation=conversations.size
        setContent {
            _Conversions(
                conversations = conversations,
                controller = controller,
                onSendButtonClick = {
                    conversations = conversations + ChatMessage(
                        message = "New Message",
                        timestamp = "now",
                        isSender = true
                    )
                },
                onAttachmentClick = {},
                onSpeechToTextRequest = {},
                navigationIcon = null
            )
        }
        textField.performTextInput("Non empty text to visible send button")
        sendButton.performClick()
        assertEquals(conversations.size,totalConversation+1)
        onNodeWithText("New Message").assertExists()//the newly send message

    }


    @Test
    fun testComponentVisibilitiesOnEmptyMessage() = runComposeUiTest {
        initializeNode()
        setUI()
        //When text is empty the Mic or Add Attachment button will be visible
        msgTypeFieldBox.assertExists(); assertTrue(msgTypeFieldBox.isDisplayed())
        screenTitle.assertExists(); assertTrue(screenTitle.isDisplayed())
        attachmentButton.assertExists(); assertTrue(attachmentButton.isDisplayed())
        speechToTextButton.assertExists();assertTrue(speechToTextButton.isDisplayed())
        sendButton.assertDoesNotExist()
    }

    @Test
    fun testComponentVisibilitiesOnNonEmptyMessage() = runComposeUiTest {
        initializeNode()
        setUI()
        //when message is not empty the send  button will be shown,attachment and mic will be hidden
        msgTypeFieldBox.assertExists(); assertTrue(msgTypeFieldBox.isDisplayed())
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
            _Conversions(
                conversations = conversations,
                controller = controller,
                onSendButtonClick = { sendClicked = true },
                onAttachmentClick = { attachmentClicked = true },
                onSpeechToTextRequest = { speechToTextClicked = true },
                navigationIcon = null
            )
        }
        //When text is empty the Mic or Add Attachment button will be visible
        attachmentButton.performClick()
        speechToTextButton.performClick()
        assertTrue { attachmentClicked && speechToTextClicked && !sendClicked }
        //When text is not empty only the send button is visible
        textField.performTextInput("Hello")
        sendButton.performClick()
        assertTrue(sendClicked)
    }


    private fun ComposeUiTest.initializeNode() {
        conversationList = onNodeWithTag("ConversationList")
        msgTypeFieldBox = onNodeWithTag("MessageInputBox")
        screenTitle = onNodeWithTag("ScreenTitle")
        sendButton = onNodeWithTag("SendMessageButton")
        attachmentButton = onNodeWithTag("AttachmentButton")
        speechToTextButton = onNodeWithTag("SpeechToTextButton")
        textField = onNodeWithTag("MessageInputField")
    }

    private fun sendMessage() {
        conversations = conversations + ChatMessage(
            message = controller.message.value,
            timestamp = "now",
            isSender = true
        )
    }

    private fun ComposeUiTest.setUI() {
        setContent {
            _Conversions(
                conversations = conversations,
                controller = controller,
                onSendButtonClick = ::sendMessage,
                onAttachmentClick = {},
                onSpeechToTextRequest = {},
                navigationIcon = null
            )
        }
    }
}

private fun dummyConversation(): List<ChatMessage> {
    val dummyConversation = mutableListOf<ChatMessage>()
    for (i in 0..10) {
        ChatMessage(
            message = "Hi! How can I help you?:$i",
            timestamp = "2023-11-03 10:05 AM",
            isSender = i % 2 == 0
        )
    }
    return dummyConversation
}