package nsd.common

import android.util.Log
import com.google.android.gms.nearby.connection.ConnectionInfo
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback
import com.google.android.gms.nearby.connection.ConnectionResolution
import com.google.android.gms.nearby.connection.ConnectionsClient
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes
import nsd.common.endpoint.EndPointInfo
import nsd.common.endpoint.EndPointStatus
import nsd.common.endpoint.EndpointList

internal class ConnectionLifeCycleCallbackImpl(
    private val advertiserList:EndpointList,
    private val _onConnectionInitiated:(endpointId: String, info: ConnectionInfo)->Unit,
) : ConnectionLifecycleCallback() {
    /** - Executed when new connection is initiated to connect with this device
     * - The connection can be accept or reject now, with  or without confirmation
     */
    override fun onConnectionInitiated(endpointId: String, info: ConnectionInfo) {
        log("Connection initiated:${info.endpointName}")
        //if for some reason the device is not added,previous
        if (!advertiserList.doesEndPointExits(endpointId)) {
            advertiserList.add(
                EndPointInfo(
                    endpointId,
                    info.endpointName,
                    EndPointStatus.Discovered
                )
            )
        }
        _onConnectionInitiated(endpointId, info)
    }

    /**
     * - Executed when a connection has been accepted ,reject or failed to accept or reject
     * - Typically trigger in response to [ConnectionsClient.acceptConnection] ,[ConnectionsClient.rejectConnection]
     */
    override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
        val isFailed = !result.status.isSuccess
        if (isFailed) {
            log("Connection Result failed:endpointId=$endpointId")
            advertiserList.updateStatus(endpointId, EndPointStatus.Discovered)
            return
        }
        when (result.status.statusCode) {
            ConnectionsStatusCodes.STATUS_OK -> {
                advertiserList.updateStatus(endpointId, EndPointStatus.Connected)
                log("Connected: $endpointId")
            }
        }
    }

    override fun onDisconnected(endpointId: String) {
        log("disconnected=$endpointId", "onDisconnected")
        advertiserList.updateStatus(endpointId, EndPointStatus.Disconnected)
    }
    @Suppress("Unused")
    private fun log(message: String, methodName: String? = null) {
        val tag = "${this@ConnectionLifeCycleCallbackImpl::class.simpleName}Log:"
        val method = if (methodName == null) "" else "$methodName()'s "
        val msg = "$method:-> $message"
        Log.d(tag, msg)
    }
}
