package socket.protocol.data_communication

 abstract class TextMessageProtocol {
    companion object {
        const val IP_LENGTH: Int = 32
        const val ADDRESS_LENGTH: Int = 32
        const val TIMESTAMP_LENGTH: Int = 20
        const val FILLED_CHAR_FOR_IP = '*'
    }
}
