package nsd.common.data_communicator

import android.util.Log
import com.google.android.gms.nearby.connection.ConnectionsClient
import com.google.android.gms.nearby.connection.Payload
import com.google.android.gms.nearby.connection.PayloadCallback
import com.google.android.gms.nearby.connection.PayloadTransferUpdate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.suspendCancellableCoroutine
import nsd.common.Message
import socket.protocol.TextMessage
import socket.protocol.TextMessageDecoder
import socket.protocol.TextMessageEncoder
import kotlin.coroutines.resume

internal class DataCommunicatorImpl(
    private val connectionsClient: ConnectionsClient
) :DataCommunicator{

    private val _receivedMessage=MutableStateFlow<Message?>(null)
    override val receivedMessage=_receivedMessage.asStateFlow()

    override val payloadCallback = object : PayloadCallback() {
        override fun onPayloadReceived(endpointId: String, payload: Payload) {
            log("Payload received from endpointId: $endpointId")
            // This always gets the full data of the payload. Is null if it's not a BYTES payload.
            if (payload.type == Payload.Type.BYTES) {
                val receivedBytes = payload.asBytes()
                if (receivedBytes != null) {
                    val message = String(receivedBytes)
                    val decodedMessage = TextMessageDecoder(message).decode()
                    log("Payload received from endpointId: ${decodedMessage.toMessage()}")
                   _receivedMessage.update { decodedMessage.toMessage() }
                }

            }
        }

        override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) {
            // Bytes payloads are sent as a single chunk, so you'll receive a SUCCESS update immediately
            // after the call to onPayloadReceived().

        }
    }


    /* Byte payloads are the simplest type of payloads. They are suitable for sending simple data like messages
    or metadata up to a maximum size of Connections.MAX_BYTES_DATA_SIZE(32k bytes) Here's an example of sending a BYTES payload:*/
    override suspend fun sendMessage(msg: Message): Result<Unit> {
        return suspendCancellableCoroutine { continuation ->
            val bytesPayload = Payload.fromBytes(
                TextMessageEncoder(msg.toTextMessage()).encodeText().toByteArray()
            )
            connectionsClient
                .sendPayload(msg.sendId, bytesPayload)
                .addOnSuccessListener {
                    log("send message:success")
                    continuation.resume(Result.success(Unit))
                }
                .addOnFailureListener { e ->
                    log("send message:failed with :$e")
                    continuation.resume(Result.failure(e))
                }
        }

    }




    //TODO : Helper method ----  Helper method ----  Helper method ----  Helper method ----  Helper method
    //TODO : Helper method ----  Helper method ----  Helper method ----  Helper method ----  Helper method

    private fun TextMessage.toMessage() =
        Message(
            senderName = senderName,
            sendId = "NULL",
            receiverName = receiverName,
            timestamp = timestamp,
            body = message
        )
    private fun Message.toTextMessage() = TextMessage(
        senderName = senderName,
        receiverName = receiverName,
        timestamp = timestamp,
        message = body
    )


    private fun log(message: String, methodName: String? = null) {
        val tag = "${this@DataCommunicatorImpl::class.simpleName}Log:"
        val method = if (methodName == null) "" else "$methodName()'s "
        val msg = "$method:-> $message"
        Log.d(tag, msg)
    }
}