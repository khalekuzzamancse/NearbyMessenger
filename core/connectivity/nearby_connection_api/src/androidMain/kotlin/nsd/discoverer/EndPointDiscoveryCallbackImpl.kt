package nsd.discoverer

import android.util.Log
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback

class EndPointDiscoveryCallbackImpl(
    private val onAdvertiserFound:(endpointId: String, info: DiscoveredEndpointInfo)->Unit,
) : EndpointDiscoveryCallback() {
    override fun onEndpointFound(endpointId: String, info: DiscoveredEndpointInfo) {
        onAdvertiserFound(endpointId,info)
    }

    override fun onEndpointLost(endpointId: String) {
        log("Endpoint lost: $endpointId")
    }
    @Suppress("Unused")
    private fun log(message: String, methodName: String? = null) {
        val tag = "${this@EndPointDiscoveryCallbackImpl::class.simpleName}Log:"
        val method = if (methodName == null) "" else "$methodName()'s "
        val msg = "$method:-> $message"
        Log.d(tag, msg)
    }
}