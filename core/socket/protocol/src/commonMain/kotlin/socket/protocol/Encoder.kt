package socket.protocol


abstract class Encoder(private val msg: TextMessage) {
    abstract fun encodeName(name: String): String
    abstract fun encodeTime(time: Long): String
    @Suppress("RedundantModalityModifier")
    //Prevent so that can not override this method,will make sure single place of encoding
    final fun encode(): String {
        val senderName = encodeName(msg.senderName)
        val receiverName = encodeName(msg.receiverName ?: "")
        val timeStamp = encodeTime(msg.timestamp)
        return senderName  + receiverName + timeStamp + msg.message
    }

}