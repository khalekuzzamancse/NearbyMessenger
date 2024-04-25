package socket.peer

/**
 * Used to decouple from nested or transient module
 *
 *  @param senderIp Nullable if message is for group
 * @param senderPort Nullable if message is for group
 *
 */
data class ServerMessage(
    val senderIp: String? = null,
    val senderPort: Int? = null,
    val message: String,
    val timestamp:Long =System.currentTimeMillis()
) {
    val isGroupMessage = senderIp == null && senderPort == null
}
