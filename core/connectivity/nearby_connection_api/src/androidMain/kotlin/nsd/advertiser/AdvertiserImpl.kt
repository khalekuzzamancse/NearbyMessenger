package nsd.advertiser

import android.content.Context
import android.util.Log
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.AdvertisingOptions
import com.google.android.gms.nearby.connection.ConnectionInfo
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback
import com.google.android.gms.nearby.connection.ConnectionResolution
import com.google.android.gms.nearby.connection.ConnectionsClient
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes
import com.google.android.gms.nearby.connection.Payload
import com.google.android.gms.nearby.connection.PayloadCallback
import com.google.android.gms.nearby.connection.Strategy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import nsd.common.AuthToken
import nsd.common.endpoint.EndPointInfo
import nsd.common.endpoint.EndPointStatus
import nsd.common.PayloadCallbackImpl
import nsd.common.confirm
import kotlin.coroutines.resume

/**
 * - Prevent client module to create instance,to provide loose coupling via factory method for instance creation
 * @param name the [Advertiser]  name
 *
 */
class AdvertiserImpl internal constructor(
    private val context: Context,
    private val name: String,
) : Advertiser {
    /**
     * - This value is required for creating a new Android service.
     * - It should be globally unique, which is why we use the app's namespace name.
     * - For both advertisers and discovered devices within this app, this value must be the same.
     */
    @Suppress("PrivatePropertyName")
    private val SERVICE_ID = "kzcse.bluefimessenger.SERVICE_ID"

    private val _devices = MutableStateFlow<Set<EndPointInfo>>(emptySet())

    /** - This indicates nearby devices that we have either discovered, connected to, or have pending requests for*/
    override val discoverers = _devices.asStateFlow()

    /** - The return value indicates that the device advertise process has completed.
     * - Based on this information,UI or relevant vent information can be updated
     **/
    override suspend fun startAdvertising(): Result<Unit> {
        return suspendCancellableCoroutine { continuation ->
            val option = createOptions()
            createConnectionClient()
                .startAdvertising(name, SERVICE_ID, connectionLifecycleCallback, option)
                .addOnSuccessListener {
                    log("startAdvertising:success")
                    continuation.resume(Result.success(Unit))
                }
                .addOnFailureListener { e ->
                    log("startAdvertising:failed with :$e")
                    continuation.resume(Result.failure(e))
                }
        }
    }

    /**
     *  - Indicate  the life cycle of a connection such as new connection has been initiated,
     *  a existing connection request(initiation) has been accepted,rejected or failed with  Exception
     *  - Executed in response to  [ConnectionsClient.requestConnection]
     *  */
    private val connectionLifecycleCallback = object : ConnectionLifecycleCallback() {

        /** - Executed when new connection is initiated to connect with this device
         * - The connection can be accept or reject now, with  or without confirmation
         */
        override fun onConnectionInitiated(endpointId: String, info: ConnectionInfo) {
            log("Connection initiated:${info.endpointName}")
            if (doesEndPointExits(endpointId))
                updateDiscovered(endpointId, EndPointStatus.Initiated)
            else
                addDiscoverer(EndPointInfo(endpointId, info.endpointName, EndPointStatus.Initiated))

            CoroutineScope(Dispatchers.Default).launch {
                acceptWithConfirmation(endpointId, info)
            }

        }


        /**
         * - Executed when a connection has been accepted ,reject or failed to accept or reject
         * - Typically trigger in response to [ConnectionsClient.acceptConnection] ,[ConnectionsClient.rejectConnection]
         */
        override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
            val isFailed = !result.status.isSuccess
            if (isFailed) {
                log("Connection Result failed:endpointId=$endpointId")
                updateDiscovered(endpointId, EndPointStatus.Discovered)
                return
            }
            when (result.status.statusCode) {
                ConnectionsStatusCodes.STATUS_OK -> {
                    updateDiscovered(endpointId, EndPointStatus.Connected)
                    log("Connected: $endpointId")
                }
            }
        }

        override fun onDisconnected(endpointId: String) {
            log("disconnected=$endpointId", "onDisconnected")
            updateDiscovered(endpointId, EndPointStatus.Disconnected)
        }

    }

    private suspend fun acceptWithConfirmation(endpointId: String, info: ConnectionInfo) {
        val token = AuthToken(endpointId, info.endpointName, info.authenticationDigits)
        val confirmationResult = confirm(context, token)
        log("Confirmation result:$confirmationResult")
        val isConfirmed = confirmationResult.isSuccess
        if (isConfirmed) {
            val acceptationResult = acceptConnection(
                endpointId,
                PayloadCallbackImpl(onTextReceived = {})
            )
            log("acceptResult:$acceptationResult")
            updateDiscovered(endpointId, EndPointStatus.Connected)
        }

    }

    /**
     * - Accept and initiate connection
     * - You have to make sure to accept both adviser and discover side
     * @param payloadCallback for data communication
     */
    private suspend fun acceptConnection(
        endpointId: String,
        payloadCallback: PayloadCallback
    ): Result<Unit> {
        return suspendCancellableCoroutine { continuation ->
            createConnectionClient()
                .acceptConnection(endpointId, payloadCallback)
                .addOnSuccessListener {
                    continuation.resume(Result.success(Unit))
                }
                .addOnFailureListener { e ->
                    continuation.resume(Result.failure(e))
                }
        }

    }

    /**- Create a new instance  of  [AdvertisingOptions]  using [Strategy.P2P_STAR] topology/strategy */
    private fun createOptions() = AdvertisingOptions
        .Builder()
        .setStrategy(Strategy.P2P_STAR)
        .build()

    fun onConnectedToEndPoint(endpointId: String) {
        //send test data
        val bytesPayload = Payload.fromBytes("Hello From Advertiser:$name".toByteArray())
        createConnectionClient().sendPayload(endpointId, bytesPayload)
    }

    fun onTextReceived(text: String) {
        log("DataReceived:$text")
    }


    /**
     * - Create a new instance  of  [ConnectionsClient]
     * - [ConnectionsClient] can be used to start advertising,initiate ,accept or reject a connection
     */
    private fun createConnectionClient(): ConnectionsClient = Nearby.getConnectionsClient(context)


    //TODO: Helper methods --- Helper methods --- Helper methods --- Helper methods --- Helper methods
    //TODO: Helper methods --- Helper methods --- Helper methods --- Helper methods --- Helper methods
    //TODO: Helper methods --- Helper methods --- Helper methods --- Helper methods --- Helper methods

    override fun getDiscoverer() = _devices.value

    private fun addDiscoverer(endPointInfo: EndPointInfo) {
        _devices.update { devices -> devices + endPointInfo }
    }

    private fun doesEndPointExits(endpointId: String) =
        _devices.value.find { it.id == endpointId } != null

    private fun updateDiscovered(endpointId: String, newState: EndPointStatus) {
        _devices.update { devices ->
            devices.map {
                if (it.id == endpointId) it.copy(
                    status = newState
                ) else it
            }.toSet()
        }
    }

    @Suppress("Unused")
    fun log(message: String, methodName: String? = null) {
        val tag = "${this@AdvertiserImpl::class.simpleName}Log:"
        val method = if (methodName == null) "" else "$methodName()'s "
        val msg = "$method:-> $message"
        Log.d(tag, msg)
    }
}