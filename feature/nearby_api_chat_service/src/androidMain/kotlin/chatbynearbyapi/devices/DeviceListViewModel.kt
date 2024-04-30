package chatbynearbyapi.devices

import android.content.Context
import androidx.lifecycle.ViewModel
import chatbynearbyapi.navigation.NetworkRole
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import nearbyapi.component.common.Message
import nearbyapi.component.common.endpoint.EndPointInfo
import nearbyapi.component.common.endpoint.EndPointStatus
import nearbyapi.endpoint_role.EndPointType
import nearbyapi.endpoint_role.NearByEndpointBuilder
import peers.ui.devices.ConnectionStatus
import peers.ui.devices.NearByDevice


/**
 * - It not holding the reference of [Context],it is one time use
 */

 class DeviceListViewModel (
    context: Context,
    private val name: String,
    role: NetworkRole
) : ViewModel() {

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    private val thisTheNearByEndPoint = if (role==NetworkRole.Advertiser)
        NearByEndpointBuilder(context, name).build(EndPointType.Advertiser)
    else
        NearByEndpointBuilder(context, name).build(EndPointType.Discoverer)
    val receivedMessage=thisTheNearByEndPoint.receivedMessage


    // Assuming both advertiser and discoverer are of a type that can possibly be null
    val nearbyDevices = thisTheNearByEndPoint.endPoints.map { endPoints ->
        endPoints.mapNotNull { endpoint ->
            endpoint.toNearByDeviceOrNull()
        }
    }

    init {
        CoroutineScope(Dispatchers.Default).launch {
            thisTheNearByEndPoint.receivedMessage.collect { newMessage ->
                if (newMessage != null)
                    log("New Message:$newMessage")
            }
        }
    }

//    init {
//        CoroutineScope(Dispatchers.Default).launch {
//            nearbyDevices.collect { devices ->
//                devices.forEach { device ->
//                    if (device.connectionStatus == ConnectionStatus.Connected) {
//                        sendDummyMessage(device.id)
//                    }
//                }
//
//            }
//        }
//    }

    private suspend fun sendDummyMessage(endpointId: String) {
        repeat(4) { i ->
            val message = Message(
                senderName = name,
                sendId = endpointId,
                timestamp = System.currentTimeMillis(),
                body = "Hello From $name:${i + 10}"
            )
            thisTheNearByEndPoint.sendMessage(message)
            delay(1_000)
        }

    }
    suspend fun sendMessage(message: Message):Result<Unit>{
        val endpoints= thisTheNearByEndPoint.endPoints.value.filter { it.status==EndPointStatus.Connected }
        if (endpoints.isEmpty()) return Result.failure(Throwable("No endpoint is connected"))

        if (message.sendId==null){//sendId=null means group message
            endpoints.forEach {endPointInfo ->
                thisTheNearByEndPoint.sendMessage(message.copy(sendId = endPointInfo.id))
            }
        }
       return Result.success(Unit)//Todo :Refactor later
    }


    suspend fun scan(): Result<Unit> {
        return thisTheNearByEndPoint.scan()
    }

    fun connect(endpointId: String) {
        CoroutineScope(Dispatchers.Default).launch {
            thisTheNearByEndPoint.initiateConnection(endpointId)
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