package socket.protocol

/**
 * @param receiverName Nullable if message is for group
 * @param receiverAddress Nullable if message is for group
 */

data class TextMessage(
    val senderName: String,
    val senderAddress: String,
    val receiverName: String?=null,
    val receiverAddress: String?=null,
    val timestamp: Long,
    val message: String
)



