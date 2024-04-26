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
        assertEquals(decoded,message)
    }
    @Test
    fun `test for  group message`(){
        val message=createMessage().copy(receiverName = null, receiverAddress = null)
        val encoded= TextMessageEncoder(message).encodeText()
        println("Encoded:$encoded")
        val decoded= TextMessageDecoder(encoded).decode()
        println("Decoded:$decoded")
        assertEquals(message,decoded)
    }
    private fun createMessage()=TextMessage(
        senderAddress = "POCO C3 Unknown Address",
        senderName = "POCO C3",
        receiverAddress = "Tab Unknown Address ",
        receiverName = "Samsung Tab",
        message = "Hello Tab",
        timestamp = System.currentTimeMillis()
    )
}