package core.socket.networking

import java.util.Date

/**
 * Prevent client module to misuse this class it own purpose so the ,
 * client module be less coupling
 */

data class TextMessage internal constructor(
    val senderAddress: String,
    val senderPort:Int,
    val receiverAddress: String,
    val receiverPort:Int,
    val body: String,
    val time:Long=Date().time
)