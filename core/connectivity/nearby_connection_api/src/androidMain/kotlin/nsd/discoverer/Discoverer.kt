package nsd.discoverer

import kotlinx.coroutines.flow.StateFlow
import nsd.common.Message
import nsd.common.endpoint.EndPointInfo

interface Discoverer {

    val receivedMessage:StateFlow<Message?>
    /** - Return success or Failure wth reason*/
    suspend fun startDiscovery(): Result<Unit>

    /** - This indicates nearby devices that we have either discovered, connected to, or have pending requests for*/
    val advertisers: StateFlow<Set<EndPointInfo>>
    fun getAdvertiser(): Set<EndPointInfo>
    suspend fun initiateConnect(endpointId: String): Result<Unit>
    suspend fun sendMessage(msg: Message): Result<Unit>

}