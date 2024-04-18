package core.socket

import core.socket.datacommunication.TextDataCommunicator
import core.socket.datacommunication.TextDataCommunicatorImpl
import core.socket.networking.clinet.BasicClient
import core.socket.networking.server.BasicServer
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TextDataCommunicatorTest {

    @Test
    fun `unidirectional Single Message Send snf ReceivedTest`() = runBlocking {
        val server = startServer(32145)
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
        val server = startServer(32145)
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


    private suspend fun startServer(port: Int): BasicServer {
        val server = BasicServer(port)
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
