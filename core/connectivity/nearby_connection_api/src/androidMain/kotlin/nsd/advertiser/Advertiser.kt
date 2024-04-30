package nsd.advertiser

import kotlinx.coroutines.flow.StateFlow
import nsd.common.endpoint.EndPointInfo

interface Advertiser {
    /**- Return success or Failure wth reason */
    suspend fun startAdvertising(): Result<Unit>
    /** - This indicates nearby devices that we have either discovered, connected to, or have pending requests for*/
    val discoverers: StateFlow<Set<EndPointInfo>>
    fun getDiscoverer(): Set<EndPointInfo>

}