package socket.protocol

import java.net.Inet4Address


abstract class Encoder(private val msg: TextMessage) {
    abstract fun encodeName(name: String): String
    abstract fun encodeAddress(address: String): String
    abstract fun encodeTime(time: Long): String
    @Suppress("RedundantModalityModifier")
    //Prevent so that can not override this method,will make sure single place of encoding
    final fun encode(): String {
        val senderName = encodeName(msg.senderName)
        val senderAddress = encodeAddress(msg.senderAddress)
        val receiverName = encodeName(msg.receiverName ?: "")
        val receiverAddress = encodeAddress(msg.receiverAddress ?: "")
        val timeStamp = encodeTime(msg.timestamp)
        return senderName + senderAddress + receiverName + receiverAddress + timeStamp + msg.message
    }

}