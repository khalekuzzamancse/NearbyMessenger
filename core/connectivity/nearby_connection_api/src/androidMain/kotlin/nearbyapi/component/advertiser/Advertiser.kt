package nearbyapi.component.advertiser

import kotlinx.coroutines.flow.StateFlow
import nearbyapi.component.common.Message
import nearbyapi.component.common.endpoint.EndPointInfo

interface Advertiser {
    val receivedMessage:StateFlow<Message?>
    /**- Return success or Failure wth reason */
    suspend fun startAdvertising(): Result<Unit>

    /** - This indicates nearby devices that we have either discovered, connected to, or have pending requests for*/
    val discoverers: StateFlow<Set<EndPointInfo>>
    fun getDiscoverer(): Set<EndPointInfo>
   suspend fun sendMessage(msg: Message):Result<Unit>


}