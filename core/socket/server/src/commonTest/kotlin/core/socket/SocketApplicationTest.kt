package core.socket

import core.socket.peer.DeviceRole
import core.socket.peer.SocketApplication
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SocketApplicationTest {
    @Test
    fun `unidirectional message send from client to server test`() = runBlocking {
        val serverApp = createServerApp()
        val clientApp = createClientApp(serverApp.address, serverApp.port)
//        //send message to server from client
        val sendMessage = "Hello"
        val sendResult = clientApp.sendMessage(sendMessage)
        assertTrue(actual = sendResult.isSuccess)
        //async.. so better to wait for a while to  connect

        //retrieving received message from client

        try {
            withTimeout(5_000) {
                serverApp.newMessages.collect { messages ->
                    if (!messages.isNullOrEmpty()) {
                       println("${this@SocketApplicationTest::class.simpleName}ReceivedMessage: $messages")
                        assertEquals(sendMessage, messages.first())
                    }
                }
                Unit
            }
        } catch (_: Exception) {

        }
    }

    private suspend fun createServerApp(): SocketApplication {
        val server = SocketApplication(DeviceRole.Server)
        //async.. so better to wait for a while to  connect
        delay(2_000)
        assertTrue(server.address != null)
        assertTrue(server.port != null)
        return server
    }

    private suspend fun createClientApp(serverAddress: String?, port: Int?): SocketApplication {
        assertTrue(actual = serverAddress != null)
        assertTrue(port != null)
        val socketApplication = SocketApplication(DeviceRole.Client(serverAddress, port))
        delay(5_000)
        return socketApplication
    }

}