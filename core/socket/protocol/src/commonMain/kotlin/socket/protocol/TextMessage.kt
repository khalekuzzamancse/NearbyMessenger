package socket.protocol

/**
 * - Not storing the sender and receiver address because the address may different based on
 * how they connect,such as bluetooth give another address,wifi give another address...
 * @param receiverName Nullable if message is for group
 */

data class TextMessage(
    val senderName: String,
    val receiverName: String?=null,
    val timestamp: Long,
    val message: String
)



