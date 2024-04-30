package nsd.common

import android.util.Log
import com.google.android.gms.nearby.connection.Payload
import com.google.android.gms.nearby.connection.PayloadCallback
import com.google.android.gms.nearby.connection.PayloadTransferUpdate

class PayloadCallbackImpl(
    private val onTextReceived:(String)->Unit,
) : PayloadCallback() {
    override fun onPayloadReceived(endpointId: String, payload: Payload) {
        log("Payload received from endpointId: $endpointId")
        // This always gets the full data of the payload. Is null if it's not a BYTES payload.
        if (payload.type == Payload.Type.BYTES) {
            val receivedBytes = payload.asBytes()
            if (receivedBytes!=null)
            onTextReceived(String(receivedBytes))
        }
    }

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
