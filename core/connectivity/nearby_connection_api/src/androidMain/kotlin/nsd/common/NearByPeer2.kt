package nsd.common

import android.content.Context
import android.util.Log
import com.google.android.gms.nearby.connection.ConnectionInfo
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback
import com.google.android.gms.nearby.connection.ConnectionResolution
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import nsd.common.endpoint.EndPointInfo
import nsd.common.endpoint.EndPointStatus

abstract class NearByPeer2(
    private val context: Context,
) {
    @Suppress("PropertyName")
    val SERVICE_ID = "kzcse.bluefimessenger.SERVICE_ID"

    /**
     * Denoted the nearby devices,that we either discovered,connected or request pending
     */
    private val _devices = MutableStateFlow<Set<EndPointInfo>>(emptySet())
    val devices = _devices.asStateFlow()
    fun getDevices()=_devices.value

    /**
     * - In case of Advertiser,add when startA
     * - In case of Discovered add new device when discovered
     */
    private fun addDevice(endPointInfo: EndPointInfo) {
        _devices.update { devices -> devices + endPointInfo }
    }
    private fun removeFromPending(endpointId: String)=
        updateDeviceStatus(endpointId, EndPointStatus.Disconnected)
    private fun updateDeviceStatus(endpointId: String, newState: EndPointStatus){
        _devices.update { devices ->
            devices.map {
                if (it.id == endpointId) it.copy(
                    status = newState
                ) else it
            }.toSet()
        }
    }
    private fun markAsConnected(endpointId: String)=updateDeviceStatus(endpointId, EndPointStatus.Connected)
    /** Called when connected to an Endpoint. Override this method to act on the event. */
    abstract fun onConnectedToEndpoint(endpointId: String)
    /** Called when TextMessage received as form of byte array. Override this method to act on the event. */
    abstract fun onTextReceived(text: String)

    /**
     * - Will notify about incoming connection request,connection success,fail or ...
     */

    protected val connectionLifecycleCallback = object : ConnectionLifecycleCallback() {
        /** - Executed when there is a connection request*/
        override fun onConnectionInitiated(endpointId: String, info: ConnectionInfo) {
            //adding the device as pending request
            addDevice(EndPointInfo(endpointId, info.endpointName, EndPointStatus.Initiated))

        }

        /*
        Executed when there is a connection request result available such as connection is accepted or rejected
        */
        override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
            val isFailed = !result.status.isSuccess
            if (isFailed) {
                removeFromPending(endpointId)
                return
            }
            when (result.status.statusCode) {
                ConnectionsStatusCodes.STATUS_OK -> {
                    markAsConnected(endpointId)
                    log("Connected: $endpointId")
                    onConnectedToEndpoint(endpointId)
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
        override fun onDisconnected(endpointId: String) {
        }
    }
    private val payloadCallback = PayloadCallbackImpl(
        onTextReceived = ::onTextReceived
    )

    @Suppress("Unused")
    fun log(message: String, methodName: String? = null) {
        val tag = "${this::class.simpleName}Log:"
        val method = if (methodName == null) "" else "$methodName()'s "
        val msg = "$method:-> $message"
        Log.d(tag, msg)
    }

}