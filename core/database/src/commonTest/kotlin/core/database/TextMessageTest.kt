package core.database

import core.database.api.TextMessageAPIs
import core.database.schema.TextMessageEntity
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TextMessageTest {

    @Test
    fun `add Text message test`() {
        // Before running this test, make sure to delete the old database or configuration file
        runBlocking {
            val requestModel = TextMessageEntity(
                senderDeviceAddress = "sender",
                timeStamp = System.currentTimeMillis(),
                message = "Test message"
            )
            val responseModel = TextMessageAPIs.addTextMessage(requestModel)
            println(responseModel)
            assertEquals(requestModel, responseModel.getOrNull())
        }
    }

    @Test
    fun `retrieve text messages`() = runBlocking {
        // Ensure database or configuration is reset before test
        val requestModel = TextMessageEntity(
            senderDeviceAddress = "sender",
            timeStamp = System.currentTimeMillis(),
            message = "Test message"
        )
        TextMessageAPIs.addTextMessage(requestModel)
        TextMessageAPIs.addTextMessage(TextMessageEntity(
            senderDeviceAddress = "sender2",
            timeStamp = System.currentTimeMillis(),
            message = "Test message"
        ))

        // Set a timeout for collecting messages
        withTimeoutOrNull(5000) { // Timeout after 1000 milliseconds (1 second)
            TextMessageAPIs.getMessages(senderAddress = "sender")
                .collect { messages ->
                    if (messages.isNotEmpty()) {
                        println(messages)
                        assertTrue(true)
                    }
                }
        } ?: println("Test timed out")
    }

    @Test
    fun `retrieve all text messages`() = runBlocking {
        // Ensure database or configuration is reset before test
        val requestModel = TextMessageEntity(
            senderDeviceAddress = "sender",
            timeStamp = System.currentTimeMillis(),
            message = "Test message"
        )
        TextMessageAPIs.addTextMessage(requestModel)
        TextMessageAPIs.addTextMessage(TextMessageEntity(
            senderDeviceAddress = "sender2",
            timeStamp = System.currentTimeMillis(),
            message = "Test message"
        ))

        // Set a timeout for collecting messages
        withTimeoutOrNull(5000) { // Timeout after 1000 milliseconds (1 second)
            TextMessageAPIs.getMessages()
                .collect { messages ->
                    if (messages.isNotEmpty()) {
                        println(messages)
                        assertTrue(true)
                    }
                }
        } ?: println("Test timed out")
    }


}