package socket.protocol.data_communication

import java.util.Date

/**
 * @param senderName Nullable if message is for group
 * @param senderPort Nullable if message is for group
 */

data class TextMessage(
    val senderName: String? = null,
    val senderPort: String? = null,
    val timestamp: Long=Date().time,
    val message: String
) {
    val isGroupMessage = senderName == null && senderPort == null
}



