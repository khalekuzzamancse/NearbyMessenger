package nearbyapi.endpoint_role

import android.content.Context
import kotlinx.coroutines.flow.StateFlow
import nearbyapi.Factory
import nearbyapi.component.common.Message
import nearbyapi.component.common.endpoint.EndPointInfo
/**
 * - Prevent client code to create instance of it to avoid tight couping
 */
class DiscovererEndPoint internal constructor(
    context: Context,
    name: String,
) :NearByEndPoint {
    private val discoverer= Factory.createDiscover(context, name)
    override val endPoints: StateFlow<Set<EndPointInfo>> =discoverer.advertisers
    override val receivedMessage: StateFlow<Message?> =discoverer.receivedMessage
    override suspend fun scan()=discoverer.startDiscovery()

    override suspend fun initiateConnection(endpointId:String)=discoverer.initiateConnect(endpointId)
    override suspend fun sendMessage(msg: Message)=discoverer.sendMessage(msg)
}