package core.socket.client.data_communication
class TextMessageDecoder(
    private val message: String
) : TextMessageProtocol() {

    private fun decodeIp(): String? {
        val decoded = message.take(IP_LENGTH).filter { it != FILLED_CHAR_FOR_IP }
        return decoded.ifEmpty { null }
    }

    private fun decodePort(): Int? {
        val decoded = message.drop(IP_LENGTH).take(PORT_LENGTH).toInt()
        return if (decoded == 0)
            null
        else
            decoded
    }

    private fun decodeTimeStamp() = message.drop(IP_LENGTH + PORT_LENGTH).take(TIMESTAMP_LENGTH).toLong()
    private fun decodeMessage() = message.drop(IP_LENGTH + PORT_LENGTH + TIMESTAMP_LENGTH)

    fun decode() = TextMessage(
        decodeIp(), decodePort(), decodeTimeStamp(), decodeMessage()
    )


    override fun toString() = decode().toString()

}