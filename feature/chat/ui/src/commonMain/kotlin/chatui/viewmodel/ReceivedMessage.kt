package chatui.viewmodel

data class ReceivedMessage(
    val senderName:String,
    val message: String,
    val timestamp: Long,
)