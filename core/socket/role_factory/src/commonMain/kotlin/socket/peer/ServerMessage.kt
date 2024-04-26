package socket.peer

/**
 * Used to decouple from nested or transient module
 * @param receiverName Nullable if message is for group
 * @param receiverAddress Nullable if message is for group
*/

data class ServerMessage(
    val senderName: String,
    val senderAddress: String,
    val receiverName: String?=null,
    val receiverAddress: String?=null,
    val message: String,
    val timestamp:Long
)
