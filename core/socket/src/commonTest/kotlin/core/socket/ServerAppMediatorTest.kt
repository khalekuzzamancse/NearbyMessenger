package core.socket

import core.socket.networking.TextMessage
import core.socket.networking.server.ServerAppMediator
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ServerAppMediatorTest {

    @Test
    fun test() {
        runBlocking {
            val sendMessage = TextMessage(
                senderAddress = "192.sender",
                receiverAddress = "192.receiver",
                message = "HI",
            )
            val mediator = ServerAppMediator()
               val result= mediator.sendMessage(sendMessage)
                assertTrue(result.isSuccess)
                val receivedMessage = mediator.readReceivedData()
                delay(4_000)
                assertEquals(sendMessage, receivedMessage)

        }
    }
}