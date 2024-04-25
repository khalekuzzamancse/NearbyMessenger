package socket.server.data_communication

internal abstract class TextMessageProtocol {
    companion object {
        const val IP_LENGTH: Int = 32
        const val PORT_LENGTH: Int = 8
        const val TIMESTAMP_LENGTH: Int = 20
        const val FILLED_CHAR_FOR_IP = '*'
    }
}
