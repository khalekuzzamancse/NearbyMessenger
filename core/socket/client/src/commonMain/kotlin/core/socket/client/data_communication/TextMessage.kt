package core.socket.client.data_communication

import java.util.Date

/**
 * @param senderIp Nullable if message is for group
 * @param senderPort Nullable if message is for group
 */

data class TextMessage(
    val senderIp: String? = null,
    val senderPort: Int? = null,
    val timestamp: Long=Date().time,
    val message: String
) {
    val isGroupMessage = senderIp == null && senderPort == null
}



