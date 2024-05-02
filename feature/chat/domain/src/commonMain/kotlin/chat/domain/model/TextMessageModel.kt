package chat.domain.model

/**
 * Need not to to store receiver address,because device itself is the receiver
 * @param participantsName either who send message to this or device send message to whom
 */
data class TextMessageModel(
    val participantsName:String,
    val message: String,
    val timeStamp: Long,
    val deviceRole: TextMessageModelRole
)
