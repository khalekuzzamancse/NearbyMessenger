package core.socket

import core.socket.networking.clinet.BasicClient
import core.socket.networking.server.BasicServer
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertTrue

class BasicClientTest {
    @Test
    fun `connecting to server after sever start`() {
        runBlocking {
            val server= BasicServer(45321)
            val serverAddress = server.address
            val serverPort = server.port
            assertTrue(actual = serverAddress != null)
            val client= BasicClient(serverAddress, serverPort)
           val result=client.connect(timeoutSeconds = 5)
            assertTrue(result.isSuccess)
            //Async operation,may not evaluated immediate so use delay
            //after connecting the serverSocket will be initialed
            delay(2_000)
            assertTrue( client.getConnectedServer()!=null)
        }
    }
}