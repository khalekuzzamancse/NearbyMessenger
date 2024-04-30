package chatbynearbyapi.devices

import android.content.Context
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import nsd.Factory
import nsd.advertiser.Advertiser
import nsd.common.endpoint.EndPointInfo
import nsd.common.endpoint.EndPointStatus
import nsd.discoverer.Discoverer
import peers.ui.devices.ConnectionStatus
import peers.ui.devices.NearByDevice


/**
 * - It not holding the reference of [Context],it is one time use
 */

class DeviceListViewModel(context: Context, name: String, isAdvertiser: Boolean) : ViewModel() {

    private val _message = MutableStateFlow<String?>(null)
    val errorMessage = _message.asStateFlow()
    private val advertiser: Advertiser? =
        if (isAdvertiser) Factory.createAdvertiser(context, name) else null
    private val discoverer: Discoverer? =
        if (!isAdvertiser) Factory.createDiscover(context, name) else null

    // Assuming both advertiser and discoverer are of a type that can possibly be null
    val nearbyDevices = (advertiser?.discoverers ?: discoverer?.advertisers)?.map { endPoints ->
        endPoints.mapNotNull { endpoint ->
            endpoint.toNearByDeviceOrNull()
        }
    } ?: flowOf(emptyList())


    suspend fun scan(): Result<Unit> {
        return (advertiser?.startAdvertising())
            ?: (discoverer?.startDiscovery() ?: Result.failure(Throwable("Failed to scan")))
    }

    fun connect(endpointId: String) {
        CoroutineScope(Dispatchers.Default).launch {
            discoverer?.initiateConnect(endpointId)
        }
    }


    //TODO :Helper method --- Helper method --- Helper method --- Helper method ---

    private fun EndPointInfo.toNearByDeviceOrNull(): NearByDevice? {
        return try {
            NearByDevice(
                name = name,
                id = id,
                connectionStatus = status.toConnectionStatus()
            )
        } catch (e: Exception) {
            null // or log the error if needed
        }
    }

    private fun EndPointStatus.toConnectionStatus(): ConnectionStatus {
        return when (this) {
            EndPointStatus.Connected -> ConnectionStatus.Connected
            EndPointStatus.Connecting -> ConnectionStatus.Connecting
            else -> ConnectionStatus.NotConnected
        }
    }

    @Suppress("Unused")
    private fun log(message: String, methodName: String? = null) {
        val tag = "${this@DeviceListViewModel::class.simpleName}Log:"
        val method = if (methodName == null) "" else "$methodName()'s "
        println("$tag:$method:-> $message")
    }

}