package nearbyapi.component.common

/**
 * @param receiverName nullable for group message
 * @param sendId is the EndpointId
 */
data class Message(
    val senderName: String,
    val sendId: String,
    val receiverName: String?=null,
    val timestamp: Long,
    val body: String
)
