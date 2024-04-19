package core.socket.networking

import java.nio.charset.StandardCharsets
import java.util.Date

/**
 * Prevent client module to misuse this class it own purpose so the ,
 * client module be less coupling
 */

data class TextMessage internal constructor(
    val senderAddress: String,
    val receiverAddress: String,
    val message: String,
    val time:Long=Date().time
)