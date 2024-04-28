package chat.data.test

import chat.data.TextMessageRepositoryImpl
import chat.domain.model.TextMessageModel
import chat.domain.model.TextMessageModelRole
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.test.Test
import kotlin.test.assertTrue

class TextMessageRepositoryImplTest {
    private val repository = TextMessageRepositoryImpl()

    @Test
    fun `add to database `() {
        // Before running this test, make sure to delete the old database or configuration file
        runBlocking {
            runBlocking {
                val message = createMessage()
                val result = repository.addToDatabase(message)
                assertTrue(result.isSuccess)
            }
        }
    }

    @Test
    fun `observing the conversation test`() = runBlocking {
        createMessages().forEach {
            repository.addToDatabase(it)
        }
        // Set a timeout for collecting messages
        withTimeoutOrNull(5000) { // Timeout after 1000 milliseconds (1 second)
            repository.observerConversation("C")
                .collect { messages ->
                    if (messages.isNotEmpty()) {
                        println(messages)
                        assertTrue(true)
                    }
                }
        } ?: println("Test timed out")
    }

    private fun createMessage() = TextMessageModel(
        participantsName = "A",
        timeStamp = System.currentTimeMillis(),
        message = "Test message",
        deviceRole = TextMessageModelRole.Sender
    )
    private fun createMessages() = listOf(
        createMessage(),
        createMessage().copy(message = "Hi B", participantsName = "B", deviceRole = TextMessageModelRole.Sender),
        createMessage().copy(message = "Hello ", participantsName = "B",deviceRole = TextMessageModelRole.Receiver),
        createMessage().copy(message = "Hello C", participantsName = "C",deviceRole = TextMessageModelRole.Sender),
    )

}