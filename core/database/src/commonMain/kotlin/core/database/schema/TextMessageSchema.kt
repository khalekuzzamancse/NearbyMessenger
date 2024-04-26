package core.database.schema

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

/**
 * Converts directly to a table or equivalent structure.
 * This should not be exposed as a public API. Instead, use separate classes for exposing data
 * and accepting input for loose coupling.
Additionally, this class is a subclass of [RealmObject]. Exposing it would require clients to depend on Realm,
leading to an undesirable architecture.

 */
//Need to store the receiver device address,because the current device itself is the receiver
internal class TextMessageSchema : RealmObject {
    var senderDeviceAddress: String = "0"
    var message: String = "0"
    var timeStamp: Long = 0


    fun toEntity() = TextMessageEntity(
        senderDeviceAddress = senderDeviceAddress,
        timeStamp = timeStamp,
        message = message,

    )

    override fun toString(): String {
        return "TextMessageEntity(senderId='$senderDeviceAddress', msg='$message', timeStamp=$timeStamp)"
    }
}

/**
 * Used for exposing data to and accepting input from clients.
 * This is analogous to JSON responses or requests in backend services.
 */
data class TextMessageEntity(
    val senderDeviceAddress: String,
    val timeStamp: Long,
    val message: String,
)