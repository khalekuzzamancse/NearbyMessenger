package socket.protocol.data_communication

class TextMessageDecoder(
    private val message: String
) : TextMessageProtocol() {

    private fun decodeIp(): String? {
        val decoded = message.take(IP_LENGTH).filter { it != FILLED_CHAR_FOR_IP }
        return decoded.ifEmpty { null }
    }

    private fun decodeAddress(): String? {
        return message.drop(IP_LENGTH).take(ADDRESS_LENGTH).ifEmpty { null }
    }

    private fun decodeTimeStamp() = message.drop(IP_LENGTH + ADDRESS_LENGTH).take(TIMESTAMP_LENGTH).toLong()
    private fun decodeMessage() = message.drop(IP_LENGTH + ADDRESS_LENGTH + TIMESTAMP_LENGTH)

    fun decode() = TextMessage(
        decodeIp(), decodeAddress(), decodeTimeStamp(), decodeMessage()
    )


    override fun toString() = decode().toString()

}