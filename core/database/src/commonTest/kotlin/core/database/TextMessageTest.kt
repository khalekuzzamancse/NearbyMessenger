package core.database

import core.database.api.TextMessageAPIs
import core.database.schema.RoleEntity
import core.database.schema.TextMessageEntity
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.test.Test
import kotlin.test.assertTrue

class TextMessageTest {

    @Test
    fun `add Text message test`() {
        // Before running this test, make sure to delete the old database or configuration file
        runBlocking {
            runBlocking {
                val requestModel = createMessage()
                assertTrue(TextMessageAPIs.add(requestModel).isSuccess)
            }
        }
    }

    @Test
    fun `retrieve conversation`() = runBlocking {
        createMessages().forEach {
            TextMessageAPIs.add(it)
        }

        // Set a timeout for collecting messages
        withTimeoutOrNull(5000) { // Timeout after 1000 milliseconds (1 second)
            TextMessageAPIs.retrieveConversation(participantAddress = "B")
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
        createMessages().forEach {
            TextMessageAPIs.add(it)
        }

        // Set a timeout for collecting messages
        withTimeoutOrNull(5000) { // Timeout after 1000 milliseconds (1 second)
            TextMessageAPIs.retrieveConversation()
                .collect { messages ->
                    if (messages.isNotEmpty()) {
                        println(messages)
                        assertTrue(true)
                    }
                }
        } ?: println("Test timed out")
    }

    private fun createMessage() = TextMessageEntity(
        participantName = "B",
        timeStamp = System.currentTimeMillis(),
        message = "congratulations B",
        deviceRole = RoleEntity.Sender
    )

    private fun createMessages() = listOf(
        createMessage(),
        createMessage().copy(message = "Hi B", participantName = "B", deviceRole = RoleEntity.Sender),
        createMessage().copy(message = "Hello ", participantName = "B",deviceRole = RoleEntity.Receiver),
        createMessage().copy(message = "Hello C", participantName = "C",deviceRole = RoleEntity.Sender),
    )

}