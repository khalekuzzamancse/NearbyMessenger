package socket.protocol

import socket.protocol.TextMessageProtocol.DEVICE_NAME_LENGTH
import socket.protocol.TextMessageProtocol.FILLED_CHAR
import socket.protocol.TextMessageProtocol.TIMESTAMP_LENGTH

class TextMessageDecoder(
    private val message: String
)  {

    private fun decodeSenderName() = message.take(DEVICE_NAME_LENGTH).filter { it != FILLED_CHAR }
    private fun decodeReceiverName():String? = message
        .drop(DEVICE_NAME_LENGTH)
        .take(DEVICE_NAME_LENGTH)
        .filter { it != FILLED_CHAR }
        .ifEmpty { return null }



    private fun decodeTimeStamp() = message
        .drop(DEVICE_NAME_LENGTH+ DEVICE_NAME_LENGTH )
        .take(TIMESTAMP_LENGTH).toLong()

    private fun decodeMessage() = message
        .drop(DEVICE_NAME_LENGTH  + DEVICE_NAME_LENGTH  + TIMESTAMP_LENGTH)

    fun decode() = TextMessage(
        senderName = decodeSenderName(),
        receiverName = decodeReceiverName(),
        timestamp = decodeTimeStamp(),
        message = decodeMessage(),

        )


    override fun toString() = decode().toString()

}