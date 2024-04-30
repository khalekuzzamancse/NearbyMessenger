package nsd.common

import android.content.Context
import android.util.Log
import com.google.android.gms.nearby.connection.ConnectionInfo
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback
import com.google.android.gms.nearby.connection.ConnectionResolution
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes

class ConnectionLifeCycleCallbackImpl(
    private val context: Context,
    private val onAcceptConfirm: (endpointId:String) -> Unit,
    private val onConnected: (endpointId:String) -> Unit,

    private val onDisconnectedFrom:(endpointId:String)->Unit,
) : ConnectionLifecycleCallback() {
    //Executed when there is a connection request form Discover device
    override fun onConnectionInitiated(endpointId: String, connectionInfo: ConnectionInfo) {
        log("Connection initiated: $endpointId")
        //When a new connection request arrived,Confirm/Authenticate before accept
        showConfirmationDialog(
            context = context,
            endpointId = endpointId,
            endpointName = connectionInfo.endpointName,
            authDigit = connectionInfo.authenticationDigits,
            onConfirm = {
                log("Connection confirm: $endpointId")
                onAcceptConfirm(endpointId)

            }
        )
    }

    /*
    Executed when there is a connection request result available such as connection is accepted or rejected
    */
    override fun onConnectionResult(endpointId: String, resolution: ConnectionResolution) {
        when (resolution.status.statusCode) {
            ConnectionsStatusCodes.STATUS_OK -> {
                log("Connected: $endpointId")
                onConnected(endpointId)
            }
            ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED -> {
                log("Connection rejected by endpoint: $endpointId")
            }
            ConnectionsStatusCodes.STATUS_ERROR -> {
                log("Error establishing connection with endpointId: $endpointId")
            }
        }
    }
    // We've been disconnected from this endpoint. No more data can be  sent or received
    override fun onDisconnected(endpointId: String)=onDisconnectedFrom(endpointId)

    @Suppress("Unused")
    private fun log(message: String, methodName: String? = null) {
        val tag = "${this@ConnectionLifeCycleCallbackImpl::class.simpleName}Log:"
        val method = if (methodName == null) "" else "$methodName()'s "
        val msg = "$method:-> $message"
        Log.d(tag, msg)
    }
}