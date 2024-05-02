package wifidirect

import android.content.Context
import android.net.wifi.WifiManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import wifidirect.broadcast.WifiDirectBroadcastReceiver
import wifidirect.connection.ConnectionManager


/*
/*
Through out the whole application we need to the manage it,
but all application may not observe it such as all the screens may not want to listen the broadcast,
so do not make it directly singleton but you can less reference of it
 */

 */

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
class BroadcastNConnectionHandler(
    context: Context,
) {

    private val connectionManager = ConnectionManager(context)
    val wifiDirectConnectionInfo=connectionManager.wifiDirectConnectionInfo
    fun getThisDeviceInfo()=connectionManager.getThisDeviceInfo()


    //WifiManager is used to enable or disable the wifi
    private val wifiManager =
        context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

    private val _isWifiEnabled = MutableStateFlow(wifiManager.isWifiEnabled)
    val isWifiEnabled = _isWifiEnabled.asStateFlow()
    val nearByDevices = connectionManager.nearbyDevices

    fun updateConnectedDeviceInfo() = connectionManager.updateConnectedDeviceInfo()


    companion object {
        private const val TAG = "WifiAndBroadcastHandlerClass: "
    }


    private val broadcastManager = WifiDirectBroadcastReceiver(
        context = context,
        onStateChangeAction = {
            Log.d(TAG, "onStateChange")
        },
        onConnectionChangeAction = {
            Log.d(TAG, "onConnectionChange")
            connectionManager.updateConnectedDeviceInfo()
        },
        onPeersChangeAction = {
            //  Log.d(TAG, " onPeersChange")
        },
        onThisDeviceChangeAction = {
            Log.d(TAG, "  onThisDeviceChange")
        },
        onWifiStateChangedAction = { wifiState ->

            when (wifiState) {
                WifiManager.WIFI_STATE_ENABLED -> {
                    _isWifiEnabled.value = true
                }

                WifiManager.WIFI_STATE_DISABLED -> {
                    _isWifiEnabled.value = false
                }
            }
        }

    )

    fun disconnectAll() = connectionManager.disconnect()

    fun registerBroadcast() = broadcastManager.register()

    fun unregisterBroadcast() = broadcastManager.unregister()

    fun scanDevice() = connectionManager.startScanning()
    fun connectTo(address: String) = connectionManager.connectWith(address)
    @Suppress("Unused")
    private fun log(message: String, methodName: String? = null) {
        val tag = "${this@BroadcastNConnectionHandler::class.simpleName}Log:"
        val method = if (methodName == null) "" else "$methodName()'s "
        val msg = "$method:-> $message"
        Log.d(tag, msg)
    }
}

