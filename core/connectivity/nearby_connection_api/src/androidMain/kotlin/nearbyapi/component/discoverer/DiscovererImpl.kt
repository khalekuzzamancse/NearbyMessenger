package nearbyapi.component.discoverer

import android.content.Context
import android.util.Log
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.ConnectionInfo
import com.google.android.gms.nearby.connection.ConnectionsClient
import com.google.android.gms.nearby.connection.DiscoveryOptions
import com.google.android.gms.nearby.connection.Strategy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import nearbyapi.component.common.ConnectionAcceptor
import nearbyapi.component.common.ConnectionLifeCycleCallbackImpl
import nearbyapi.component.common.data_communicator.DataCommunicatorImpl
import nearbyapi.component.common.Message
import nearbyapi.component.common.endpoint.EndPointStatus
import nearbyapi.component.common.endpoint.EndpointList
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
    private val connectionsClient = Nearby.getConnectionsClient(context)
    private val dataCommunicator= DataCommunicatorImpl(connectionsClient)
    private val payloadCallback=dataCommunicator.payloadCallback


    /**
     * - This value is required for creating a new Android service.
     * - It should be globally unique, which is why we use the app's namespace name.
     * - For both advertisers and discovered devices within this app, this value must be the same.
     */
    @Suppress("PrivatePropertyName")
    private val SERVICE_ID = "kzcse.bluefimessenger.SERVICE_ID"

    /** - This indicates nearby devices that we have either discovered, connected to, or have pending requests for*/
    override val advertisers = advertiserList.endpoints


    override val receivedMessage=dataCommunicator.receivedMessage

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
    private val endpointDiscoveryCallback = EndPointDiscoveryCallbackImpl(advertiserList)


    /**
     * - These callback will trigger in response to  [ConnectionsClient.requestConnection]
     *  - Indicate  the life cycle of a connection such as new connection has been initiated,a existing connection request(initiation)
     *  has been accepted,rejected or failed with  Exception
     *  */

    private val connectionLifecycleCallback = ConnectionLifeCycleCallbackImpl(
        advertiserList = advertiserList,
        _onConnectionInitiated = { endpointId, info ->
            //confirm for accept
            CoroutineScope(Dispatchers.Default).launch {
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
        val acceptor = ConnectionAcceptor(context, createConnectionClient(), advertiserList, payloadCallback = payloadCallback)
        acceptor.acceptWithConfirmation(endpointId, info)
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
     * - Create a new instance  of  [ConnectionsClient]
     * - [ConnectionsClient] can be used to start discovering,initiate ,accept or reject a connection
     * */
    private fun createConnectionClient(): ConnectionsClient {
//        return  Nearby.getConnectionsClient(context)
        return connectionsClient
    }

    override suspend fun sendMessage(msg: Message)=dataCommunicator.sendMessage(msg)


    //TODO: Helper methods --- Helper methods --- Helper methods --- Helper methods --- Helper methods
    //TODO: Helper methods --- Helper methods --- Helper methods --- Helper methods --- Helper methods
    //TODO: Helper methods --- Helper methods --- Helper methods --- Helper methods --- Helper methods
    override fun getAdvertiser() = advertisers.value

    @Suppress("Unused")
    private fun log(message: String, methodName: String? = null) {
        val tag = "${this@DiscovererImpl::class.simpleName}Log:"
        val method = if (methodName == null) "" else "$methodName()'s "
        val msg = "$method:-> $message"
        Log.d(tag, msg)
    }
}