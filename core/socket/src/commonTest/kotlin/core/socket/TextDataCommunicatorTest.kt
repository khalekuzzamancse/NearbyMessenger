package core.socket

import core.socket.datacommunication.TextDataCommunicator
import core.socket.datacommunication.TextDataCommunicatorImpl
import core.socket.networking.clinet.BasicClient
import core.socket.networking.server.BasicServer
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TextDataCommunicatorTest {

    @Test
    fun `unidirectional Single Message Send snf ReceivedTest`() = runBlocking {
        val server = startServer()
        val client = connectClient(server)
        val clientCommunicator = createDataCommunicatorForClient(client)
        val serverCommunicator = createDataCommunicatorForServer(server)
        val sendMessage = "Hello"
        clientCommunicator.sendMessage(sendMessage)
        val receivedMessage = serverCommunicator.retrieveReceivedData()
        assertEquals(
            expected = sendMessage,
            actual = receivedMessage.first(),
            message = "ReceivedMessage:$receivedMessage"
        )
    }

    @Test
    fun `unidirectional multiple Message Send snf ReceivedTest`() = runBlocking {
        val server = startServer()
        val client = connectClient(server)
        val clientCommunicator = createDataCommunicatorForClient(client)
        val serverCommunicator = createDataCommunicatorForServer(server)
        val sendMessages = listOf("HI", "Hello", "How are you")
        sendMessages.forEach { msg ->
            clientCommunicator.sendMessage(msg)
        }
        val receivedMessages = serverCommunicator.retrieveReceivedData()

        assertEquals(
            expected = sendMessages,
            actual = receivedMessages,
            message = "ReceivedMessage:$receivedMessages"
        )
    }
    @Test
    fun `bidirectional multiple Message Send and Receive Test`() = runBlocking {

        val server = startServer()
        val client = connectClient(server)

        // Create communicators for both client and server
        val clientCommunicator = createDataCommunicatorForClient(client)
        val serverCommunicator = createDataCommunicatorForServer(server)

        // Define messages for both client and server
        val clientMessages = listOf("HI", "How are you?")
        val serverMessages = listOf("Hello", "I am fine, thanks!")

        // Coroutine to send messages from client
        launch {
            clientMessages.forEach { msg ->
                clientCommunicator.sendMessage(msg)
            }
        }

        // Coroutine to send messages from server
        launch {
            serverMessages.forEach { msg ->
                serverCommunicator.sendMessage(msg)
            }
        }

        // Simulate a short delay for messages to be sent and received
        delay(5000)

        // Retrieve received messages
        val receivedByServer = serverCommunicator.retrieveReceivedData()
        val receivedByClient = clientCommunicator.retrieveReceivedData()
        println("${this::class.simpleName}:receivedByServer::$receivedByServer")
        println("${this::class.simpleName}:receivedByClient::$receivedByClient")
        // Assertions to verify each side received what was sent
        assertEquals(
            expected = clientMessages,
            actual = receivedByServer,
            message = "Server should receive correct messages from client"
        )
        assertEquals(
            expected = serverMessages,
            actual = receivedByClient,
            message = "Client should receive correct messages from server"
        )
    }
    @Test
    fun `sequential bidirectional message send and receive test`() = runBlocking {

        val server = startServer()
        val client = connectClient(server)

        // Create communicators for both client and server
        val clientCommunicator = createDataCommunicatorForClient(client)
        val serverCommunicator = createDataCommunicatorForServer(server)

        // Simulate a conversational message exchange
        val clientFirstMessage = "HI"
        val serverFirstResponse = "Hello, who's there?"

        // Client sends first message
        clientCommunicator.sendMessage(clientFirstMessage)
        // Server receives first message
        var receivedByServer = serverCommunicator.retrieveReceivedData()
        assertEquals(listOf(clientFirstMessage), receivedByServer, "Server should correctly receive the client's first message")

        // Server sends response
        serverCommunicator.sendMessage(serverFirstResponse)
        // Client receives response
        var receivedByClient = clientCommunicator.retrieveReceivedData()
        assertEquals(listOf(serverFirstResponse), receivedByClient, "Client should correctly receive the server's first response")

        // Continue the conversation
        val clientSecondMessage = "It's John."
        val serverSecondResponse = "How can I help you today, John?"

        // Client sends another message
        clientCommunicator.sendMessage(clientSecondMessage)
        // Server receives second message
        receivedByServer = serverCommunicator.retrieveReceivedData()
        assertEquals(listOf(clientSecondMessage), receivedByServer, "Server should correctly receive the client's second message")

        // Server sends another response
        serverCommunicator.sendMessage(serverSecondResponse)
        // Client receives the second response
        receivedByClient = clientCommunicator.retrieveReceivedData()
        assertEquals(listOf(serverSecondResponse), receivedByClient, "Client should correctly receive the server's second response")
    }

    private suspend fun startServer(): BasicServer {
        val server = BasicServer()
        checkServerIsOk(server)
        delay(2_000)
        return server
    }

    private suspend fun connectClient(server: BasicServer): BasicClient {
        val client = BasicClient(server.address!!, server.port)
        val result = client.connect(timeoutSeconds = 5)
        assertTrue(result.isSuccess)
        delay(2_000)
        checkClientIsConnected(client)
        return client
    }

    private fun checkServerIsOk(server: BasicServer) {
        assertTrue(server.address != null, "Server address must be initialized.")
    }

    private fun checkClientIsConnected(client: BasicClient) {
        assertTrue(client.getConnectedServer() != null, "Client should be connected.")
    }

    private fun createDataCommunicatorForClient(client: BasicClient): TextDataCommunicator {
        return TextDataCommunicatorImpl(client.getConnectedServer()!!)
    }

    private fun createDataCommunicatorForServer(server: BasicServer): TextDataCommunicator {
        return TextDataCommunicatorImpl(server.getClients().first())
    }


}
