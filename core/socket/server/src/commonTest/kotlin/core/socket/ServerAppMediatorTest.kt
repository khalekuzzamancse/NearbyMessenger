package core.socket

import core.socket.networking.TextMessage
import core.socket.networking.server.ServerAppMediator
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ServerAppMediatorTest {

    @Test
    fun `unidirectional message send for singleMessage`() {
        runBlocking {
            val mediator = ServerAppMediator()
           testForParticipantAdded(mediator)//TODO :causes fail why ???
            val sendMessage=createMessage(mediator)
            delay(4_000)
            println("${this@ServerAppMediatorTest::class.simpleName}:SendMessage$sendMessage")
            val result = mediator.sendMessageToServer(sendMessage)
            assertTrue(result.isSuccess, message = "Result:${result}")
            delay(4_000)
            val receivedMessage = mediator.messages.value.first()
            println("${this@ServerAppMediatorTest::class.simpleName}:Msg${mediator.messages.value}")
            assertEquals(sendMessage, receivedMessage)
        }
    }

    private suspend fun createMessage(mediator: ServerAppMediator): TextMessage {
//        val senderAddress = mediator.participants.value.toList()[0].address
//       // val senderPort = mediator.participants.value.toList()[0].port
//        assertTrue(actual = senderAddress != null)
//        val receiverAddress = mediator.participants.value.toList()[1].address
//      //  val receiverPort = mediator.participants.value.toList()[1].port
//        assertTrue(actual = receiverAddress != null)
        return TextMessage(
            senderAddress = "senderAddress",
            senderPort = 0,
            receiverAddress = "receiverAddress",
            receiverPort = 0,
            body = "HI",
        )

    }

    private suspend fun testForParticipantAdded(mediator: ServerAppMediator) {
        mediator.createNConnectAsClient()
        mediator.createNConnectAsClient()
        mediator.createNConnectAsClient()
        delay(4_000)
        assertTrue(mediator.participants.value.size >= 3)
    }

    @Test
    fun `unidirectional multiple message send`() {
        runBlocking {
            val sendMessage = TextMessage(
                senderAddress = "192.sender",
                senderPort = 0,
                receiverAddress = "192.receiver",
                receiverPort = 0,
                body = "HI",
            )
            val sendingMessages = listOf(
                sendMessage,
                sendMessage.copy(body = "Hello"),
                sendMessage.copy(body = "How are you ?")
            )
            val mediator = ServerAppMediator()
            mediator.sendMessageToServer(sendingMessages[0])
            delay(2_000)
            mediator.sendMessageToServer(sendingMessages[1])
            delay(2_000)
            mediator.sendMessageToServer(sendingMessages[2])
            delay(4_000)
            val receivedMessages = mediator.messages.first()
            println("${this@ServerAppMediatorTest::class.simpleName}:${receivedMessages.map { it.body }}")
            assertEquals(sendingMessages, receivedMessages)
        }
    }

    @Test
    fun `checking for multiple participants`() {
        runBlocking {
            val mediator = ServerAppMediator()
            mediator.createNConnectAsClient()
            mediator.createNConnectAsClient()
            mediator.createNConnectAsClient()
            delay(4_000)
            println("${this@ServerAppMediatorTest::class.simpleName}:${mediator.participants.value}")
            assertTrue(mediator.participants.value.size == 4)//clients+server
        }
    }

    @Test
    fun `message sending  for multiple participants`() {
        runBlocking {
            val mediator = ServerAppMediator()
            mediator.createNConnectAsClient()
            mediator.createNConnectAsClient()
            mediator.createNConnectAsClient()
            delay(4_000)
            println("${this@ServerAppMediatorTest::class.simpleName}:${mediator.participants.value}")
            assertTrue(mediator.participants.value.size == 4)//clients+server
        }
    }

    @Test
    fun `sending message to different participants`() {
        runBlocking {
            val mediator = ServerAppMediator()
            mediator.createNConnectAsClient()
            mediator.createNConnectAsClient()
            mediator.createNConnectAsClient()
            delay(4_000)
            val sendMessage = TextMessage(
                senderAddress = "192.sender",
                senderPort = 0,
                receiverPort = 0,
                receiverAddress = "192.receiver",
                body = "HI",
            )
            val result = mediator.sendMessageToServer(sendMessage)
            assertTrue(result.isSuccess)
            delay(4_000)
            val receivedMessage = mediator.messages.value.first()
            assertEquals(sendMessage, receivedMessage)

        }
    }
}