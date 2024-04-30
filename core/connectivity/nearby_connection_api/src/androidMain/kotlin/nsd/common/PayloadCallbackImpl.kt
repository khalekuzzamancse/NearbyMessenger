package nsd.common

import android.util.Log
import com.google.android.gms.nearby.connection.Payload
import com.google.android.gms.nearby.connection.PayloadCallback
import com.google.android.gms.nearby.connection.PayloadTransferUpdate
import socket.protocol.TextMessage
import socket.protocol.TextMessageDecoder

internal class PayloadCallbackImpl(
    private val onTextReceived:(Message)->Unit,
) : PayloadCallback() {
    override fun onPayloadReceived(endpointId: String, payload: Payload) {
        log("Payload received from endpointId: $endpointId")
        // This always gets the full data of the payload. Is null if it's not a BYTES payload.
        if (payload.type == Payload.Type.BYTES) {
            val receivedBytes = payload.asBytes()
            if (receivedBytes!=null){
                val message=String(receivedBytes)
                val decodedMessage = TextMessageDecoder(message).decode()
                log("Payload received from endpointId: ${decodedMessage.toMessage()}")
                onTextReceived(decodedMessage.toMessage())
            }

        }
    }
    private fun TextMessage.toMessage()=
        Message(
            senderName = senderName,
            sendId = "NULL",
            receiverName = receiverName,
            timestamp = timestamp,
            body = message
        )

    override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) {

        // Bytes payloads are sent as a single chunk, so you'll receive a SUCCESS update immediately
        // after the call to onPayloadReceived().

    }
    private fun log(message: String, methodName: String? = null) {
        val tag = "${this@PayloadCallbackImpl::class.simpleName}Log:"
        val method = if (methodName == null) "" else "$methodName()'s "
        val msg = "$method:-> $message"
        Log.d(tag, msg)
    }
}
