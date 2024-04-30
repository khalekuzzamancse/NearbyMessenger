package nearbyapi.component.discoverer

import android.util.Log
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback
import nearbyapi.component.common.endpoint.EndPointInfo
import nearbyapi.component.common.endpoint.EndPointStatus
import nearbyapi.component.common.endpoint.EndpointList
/** - The callback of this instance will be executed when a new advertiser is found or when an existing one is lost */
internal class EndPointDiscoveryCallbackImpl(
    private val advertiserList: EndpointList,
) : EndpointDiscoveryCallback() {
    /** - Executed when a new advertiser is found ,advertiser list or UI can be updated based on this information
     * - Can made a new connection request using the [endpointId]
     **/
    override fun onEndpointFound(endpointId: String, info: DiscoveredEndpointInfo) {
        log("Endpoint found :${info.endpointName}")
        advertiserList.add(
            EndPointInfo(endpointId, info.endpointName, EndPointStatus.Discovered)
        )
    }

    /** - Executed when an existing endpoint is gone
     * - advertiser list or UI can be updated based on this information
     * */
    override fun onEndpointLost(endpointId: String) {
        log("Endpoint lost :$endpointId")
        advertiserList.remove(endpointId)
    }
    @Suppress("Unused")
    private fun log(message: String, methodName: String? = null) {
        val tag = "${this@EndPointDiscoveryCallbackImpl::class.simpleName}Log:"
        val method = if (methodName == null) "" else "$methodName()'s "
        val msg = "$method:-> $message"
        Log.d(tag, msg)
    }
}