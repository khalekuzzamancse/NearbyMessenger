package socket.peer

/**
 * Used to decouple from nested or transient module
 *
 *  @param senderAddress Nullable if message is for group
 * @param senderName Nullable if message is for group
 *
 */
data class ServerMessage(
    val senderAddress: String? = null,
    val senderName: String? = null,
    val message: String,
    val timestamp:Long =System.currentTimeMillis()
) {
    val isGroupMessage = senderAddress == null && senderName == null
}
