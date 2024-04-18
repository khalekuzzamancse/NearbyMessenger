package core.socket

import core.socket.networking.server.BasicServer
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.net.InetSocketAddress
import java.net.Socket
import kotlin.test.Test
import kotlin.test.assertTrue
/**
 * * Use separate port for each test,otherwise can causes: [java.net.BindException] (Address already in use: bind)
 */
class BasicServerTest {
    @Test
    fun `test server is created`() {
        runBlocking {
            val server = BasicServer(45524)
            assertTrue(server.isServerCreated())
        }
    }

    @Test
    fun `test for client connection`() {
        runBlocking {
            val server= BasicServer(45321)
            val clientSocket = Socket()
            val serverAddress = server.address
            val serverPort = server.port
            assertTrue(actual = serverAddress != null)
            clientSocket.connect(InetSocketAddress(serverAddress, serverPort))
            assertTrue(clientSocket.isConnected)
            //Asynchronous function for adding clients,delay to adding to list
            delay(3_000)
            val connectedClients=server.getClients()
            println("ClientList:$connectedClients")
            assertTrue(connectedClients.isNotEmpty())
        }
    }
}