package core.wifi_hotspot

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.runBlocking

/**
 * uses:
 * ```kotlin
 *    val context= LocalContext.current as ComponentActivity
 *     val   wifiStateMonitor = WiFiHotspotFactory.receiver.wifiStateMonitor
 *     context.lifecycle.addObserver(wifiStateMonitor)
 * ```
 */
class WifiStateMonitor(
    private val connectivityManager: ConnectivityManager
) : DefaultLifecycleObserver {

    private val _wifiConnected = MutableStateFlow(false)
    val wifiConnected = _wifiConnected.asStateFlow()

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            updateNetwork(network)
        }

        override fun onLost(network: Network) {
            _wifiConnected.value = false
        }

        @SuppressLint("MissingPermission")
        private fun updateNetwork(network: Network) {
            val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
            val isWifi = networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ?: false
            runBlocking(Dispatchers.Main) {
                _wifiConnected.value = isWifi
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        connectivityManager.registerDefaultNetworkCallback(networkCallback)
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }
}
