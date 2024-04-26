package core.database.schema

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

internal class TextMessageSchema : RealmObject {
    var participantsAddress: String = "otherDevice"
    var message: String = ""
    var timeStamp: Long = 0
    var isSender:Boolean=true
    fun toEntity() = TextMessageEntity(
        timeStamp = timeStamp,
        message = message,
        participantsAddress = participantsAddress,
        deviceRole = if (isSender)RoleEntity.Sender else RoleEntity.Receiver
    )

    override fun toString(): String {
        return "TextMessageEntity(msg='$message', timeStamp=$timeStamp,participantsAddress=$participantsAddress)"
    }
}

/**
 * Need not to to store receiver address,because device itself is the receiver
 * @param participantsAddress either who send message to this or device send message to whom
 */
data class TextMessageEntity(
    val participantsAddress: String,
    val timeStamp: Long,
    val message: String,
    val deviceRole:RoleEntity
)
