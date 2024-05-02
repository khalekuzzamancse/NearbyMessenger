package test

import socket.protocol.TextMessage
import socket.protocol.TextMessageDecoder
import socket.protocol.TextMessageEncoder
import kotlin.test.Test
import kotlin.test.assertEquals

class EncodingAndDecodingTest {
    @Test
    fun `test  for non group message`(){
        val message=createMessage()
        val encoded= TextMessageEncoder(message).encodeText()
        println("Encoded:$encoded")
        val decoded= TextMessageDecoder(encoded).decode()
        println("Decoded:$decoded")
        assertEquals(message,decoded)
    }
    @Test
    fun `test for  group message`(){
        val message=createMessage().copy(receiverName = null)
        val encoded= TextMessageEncoder(message).encodeText()
        println("Encoded:$encoded")
        val decoded= TextMessageDecoder(encoded).decode()
        println("Decoded:$decoded")
        assertEquals(message,decoded)
    }
    private fun createMessage()=TextMessage(
        senderName = "POCO C3",
        receiverName = "Samsung Tab",
        message = "Hello Tab",
        timestamp = System.currentTimeMillis()
    )
}