package core.socket.client

data class ClientMessage(
    val senderName: String,
    val senderAddress: String,
    val receiverName: String?=null,
    val receiverAddress: String?=null,
    val timestamp: Long,
    val message: String
)



