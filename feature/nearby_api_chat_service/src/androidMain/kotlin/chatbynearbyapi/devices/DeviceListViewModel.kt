package chatbynearbyapi.devices

import android.content.Context
import androidx.lifecycle.ViewModel
import chatbynearbyapi.navigation.NetworkRole
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
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

class DeviceListViewModel(
    context: Context,
    name: String,
    role: NetworkRole
) : ViewModel() {


    private val thisTheNearByEndPoint = if (role == NetworkRole.Advertiser)
        NearByEndpointBuilder(context, name).build(EndPointType.Advertiser)
    else
        NearByEndpointBuilder(context, name).build(EndPointType.Discoverer)
    val receivedMessage = thisTheNearByEndPoint.receivedMessage

    //
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    private val _isScanning= MutableStateFlow(false)
    val isScanning=_isScanning.asStateFlow()

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


    suspend fun sendMessage(message: Message): Result<Unit> {
        val endpoints =
            thisTheNearByEndPoint.endPoints.value.filter { it.status == EndPointStatus.Connected }
        if (endpoints.isEmpty()) return Result.failure(Throwable("No endpoint is connected"))

        if (message.sendId == null) {//sendId=null means group message
            endpoints.forEach { endPointInfo ->
                thisTheNearByEndPoint.sendMessage(message.copy(sendId = endPointInfo.id))
            }
        }
        return Result.success(Unit)//Todo :Refactor later
    }


    fun scan() {
        CoroutineScope(Dispatchers.Default).launch {
            val result = thisTheNearByEndPoint.scan()
            if (result.isSuccess){
                _isScanning.update { true }
                updateErrorMessage("Scan started")
            }
            else{
                updateErrorMessage("Failed to start scan;${result.exceptionOrNull()?.message}")
                _isScanning.update { false }
            }
            /*TODO:Refactor it later
            Since right now I do not know how to know currently the device is advertising or discovering or not,
            that is why going to pretend for 1 min,that the device is scanning...this is fake.
           */
            delay(60*1000L)
            _isScanning.update { false }

        }
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

    private fun updateErrorMessage(msg: String) {
        CoroutineScope(Dispatchers.Default).launch {
            _errorMessage.update { msg }
            delay(3_000)
            _errorMessage.update { null }
        }

    }

    @Suppress("Unused")
    private fun log(message: String, methodName: String? = null) {
        val tag = "${this@DeviceListViewModel::class.simpleName}Log:"
        val method = if (methodName == null) "" else "$methodName()'s "
        println("$tag:$method:-> $message")
    }

}