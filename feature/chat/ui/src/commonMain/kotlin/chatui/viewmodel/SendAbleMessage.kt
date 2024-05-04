package chatui.viewmodel

/**
 * * Prevent client to misuse it or use it client own purpose
 * @param receiverName null for group message
 */
data class SendAbleMessage (
    val message: String,
    val receiverName:String?,
    val timestamp: Long
)
