package chatui.viewmodel

data class ReceivedMessage(
    val senderName:String,
    val senderAddress:String,
    val message: String,
    val timestamp: Long,
)