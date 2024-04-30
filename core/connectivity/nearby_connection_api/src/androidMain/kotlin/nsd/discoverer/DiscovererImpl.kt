package nsd.discoverer

import android.content.Context
import android.util.Log
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.ConnectionInfo
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback
import com.google.android.gms.nearby.connection.ConnectionResolution
import com.google.android.gms.nearby.connection.ConnectionsClient
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo
import com.google.android.gms.nearby.connection.DiscoveryOptions
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback
import com.google.android.gms.nearby.connection.Payload
import com.google.android.gms.nearby.connection.PayloadCallback
import com.google.android.gms.nearby.connection.Strategy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import nsd.common.AuthToken
import nsd.common.ConnectionLifeCycleCallbackImpl2
import nsd.common.endpoint.EndPointInfo
import nsd.common.endpoint.EndPointStatus
import nsd.common.endpoint.EndpointList
import nsd.common.PayloadCallbackImpl
import nsd.common.confirm
import kotlin.coroutines.resume

/**
 * - Prevent client module to create instance,to provide loose coupling via factory method for instance creation
 * @param name used to denote the device name.this can be modified by API to resolve naming conflict
 */
class DiscovererImpl internal constructor(
    private val context: Context,
    private val name: String,
) : Discoverer {
    private val advertiserList = EndpointList()

    /**
     * - This value is required for creating a new Android service.
     * - It should be globally unique, which is why we use the app's namespace name.
     * - For both advertisers and discovered devices within this app, this value must be the same.
     */
    @Suppress("PrivatePropertyName")
    private val SERVICE_ID = "kzcse.bluefimessenger.SERVICE_ID"

    /** - This indicates nearby devices that we have either discovered, connected to, or have pending requests for*/
    override val advertisers = advertiserList.endpoints

    /** - The return value indicates that the device discovery process has completed.
     * Based on this information,you can update the user interface (UI)
     **/
    override suspend fun startDiscovery(): Result<Unit> {
        return suspendCancellableCoroutine { continuation ->
            val discoveryOptions = DiscoveryOptions.Builder().setStrategy(Strategy.P2P_STAR).build()
            createConnectionClient()
                .startDiscovery(SERVICE_ID, endpointDiscoveryCallback, discoveryOptions)
                .addOnSuccessListener {
                    log("startDiscovery: successfully")
                    continuation.resume(Result.success(Unit))
                }
                .addOnFailureListener { e ->
                    log("startDiscovery: failed :$e")
                    continuation.resume(Result.failure(e))
                }
        }
    }

    /** - The callback of this instance will be executed when a new advertiser is found or when an existing one is lost */
    private val endpointDiscoveryCallback = object : EndpointDiscoveryCallback() {
        /** - Executed when a new advertiser is found ,advertiser list or UI can be updated based on this information
         * - Can made a new connection request using the [endpointId]
         **/
        override fun onEndpointFound(endpointId: String, info: DiscoveredEndpointInfo) {
            log("Endpoint found :${info.endpointName}")
            advertiserList.add(
                EndPointInfo(
                    endpointId,
                    info.endpointName,
                    EndPointStatus.Discovered
                )
            )
        }

        /** - Executed when an existing endpoint is gone
         * - advertiser list or UI can be updated based on this information
         * */
        override fun onEndpointLost(endpointId: String) {
            log("Endpoint lost :$endpointId")
            advertiserList.remove(endpointId)
        }
    }


    /**
     * - These callback will trigger in response to  [ConnectionsClient.requestConnection]
     *  - Indicate  the life cycle of a connection such as new connection has been initiated,a existing connection request(initiation)
     *  has been accepted,rejected or failed with  Exception
     *  */

    private val connectionLifecycleCallback = ConnectionLifeCycleCallbackImpl2(
        advertiserList = advertiserList,
        _onConnectionInitiated = {endpointId, info ->
            //confirm for accept
            CoroutineScope(Dispatchers.Default).launch{
                acceptWithConfirmation(endpointId, info)
            }
        }
    )

    /**
     * - It update the [EndPointStatus] to [EndPointStatus.Connecting] to either [EndPointStatus.Connected] or [EndPointStatus.Discovered]
     * - If the connection was accepted by clicked the accept button on dialogue
     * - It will show a confirmation dialog in both advertiser and discovered
     */
    private suspend fun acceptWithConfirmation(endpointId: String, info: ConnectionInfo) {
        //Start updating to denoted try to connecting
        advertiserList.updateStatus(endpointId, EndPointStatus.Connecting)
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
            advertiserList.updateStatus(endpointId, EndPointStatus.Connected)
        } else {
            //Connected was not successful
            advertiserList.updateStatus(endpointId, EndPointStatus.Discovered)
        }

    }

    /**
     * - The return value indicates that the connection has been successfully initiated. Consequently,
     * the connection is pending and awaits either acceptance or rejection
     * - Based on this return result, the advertiser list status, UI, or other relevant information can be updated
     * - this will be used by client code
     */

    override suspend fun initiateConnect(endpointId: String): Result<Unit> {
        //update the status that connecting...,
        advertiserList.updateStatus(endpointId, EndPointStatus.Connecting)
        return suspendCancellableCoroutine { continuation ->
            createConnectionClient()
                .requestConnection(name, endpointId, connectionLifecycleCallback)
                .addOnSuccessListener {
                    continuation.resume(Result.success(Unit))
                }
                .addOnFailureListener { e ->
                    continuation.resume(Result.failure(e))
                }
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

    private fun onConnectedToEndPoint(endpointId: String) {
        log("Connection: $endpointId")
        //send test data
        val bytesPayload = Payload.fromBytes("Hello From Discover:$name".toByteArray())
        createConnectionClient().sendPayload(endpointId, bytesPayload)
    }

    /**
     * - Create a new instance  of  [ConnectionsClient]
     * - [ConnectionsClient] can be used to start discovering,initiate ,accept or reject a connection
     * */
    private fun createConnectionClient(): ConnectionsClient = Nearby.getConnectionsClient(context)


    //TODO: Helper methods --- Helper methods --- Helper methods --- Helper methods --- Helper methods
    //TODO: Helper methods --- Helper methods --- Helper methods --- Helper methods --- Helper methods
    //TODO: Helper methods --- Helper methods --- Helper methods --- Helper methods --- Helper methods
    override fun getAdvertiser() = advertisers.value


    /**
     * - In case of Advertiser,add when startA
     * - In case of Discovered add new device when discovered
     */


    @Suppress("Unused")
    private fun log(message: String, methodName: String? = null) {
        val tag = "${this@DiscovererImpl::class.simpleName}Log:"
        val method = if (methodName == null) "" else "$methodName()'s "
        val msg = "$method:-> $message"
        Log.d(tag, msg)
    }
}