package nearbyapi.component.common

/**
 * @param receiverName nullable for group message
 * @param sendId is the EndpointId.if null then all send as group message
 */

data class Message(
    val senderName: String,
    val sendId: String?=null,
    val receiverName: String?=null,
    val timestamp: Long,
    val body: String
)
