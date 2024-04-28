package core.socket.client

/**
 * @param receiverName nullable for group message
 */
data class ClientMessage(
    val senderName: String,
    val receiverName: String?=null,
    val timestamp: Long,
    val message: String
)



