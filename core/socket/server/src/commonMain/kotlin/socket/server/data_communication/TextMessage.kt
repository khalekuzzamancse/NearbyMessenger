package socket.server.data_communication

/**
 * @param senderIp Nullable if message is for group
 * @param senderPort Nullable if message is for group
 */

data class TextMessage(
    val senderIp: String? = null,
    val senderPort: Int? = null,
    val timestamp: Long,
    val message: String
) {
    val isGroupMessage = senderIp == null && senderPort == null
}



