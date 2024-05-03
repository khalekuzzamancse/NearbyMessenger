package core.wifi_hotspot

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.RouteInfo
import android.net.wifi.ScanResult
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build

class WiFiHotspotBroadcastReceiver internal constructor(
    private val wifiManager: WifiManager,
    private val connectivityManager: ConnectivityManager,
) : BroadcastReceiver() {


    val wifiStateMonitor=WifiStateMonitor(connectivityManager)
    override fun onReceive(context: Context?, intent: Intent?) {
        //Right now,nothing to do
    }


    fun  isWifiEnabled()=wifiManager.isWifiEnabled
    @SuppressLint("MissingPermission")
    fun isWifiConnected(): Boolean {
        val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        return  capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ?: false
    }
    @SuppressLint("MissingPermission")
    fun getHotspotDefaultGateway(): String? {
        try {
            val network = connectivityManager.activeNetwork
            val linkProperties = connectivityManager.getLinkProperties(network)
            linkProperties?.routes?.forEach { routeInfo: RouteInfo ->
                if (routeInfo.isDefaultRoute) {
                    return routeInfo.gateway?.hostAddress
                }
            }
        } catch (_: Exception) {
        }
        return null
    }


    @Suppress("Unused")
    private fun log(message: String, methodName: String? = null) {
        val tag = "${this@WiFiHotspotBroadcastReceiver::class.simpleName}Log:"
        val method = if (methodName == null) "" else "$methodName()'s "
        val msg = "$method:-> $message"
        println(tag + msg)
    }


}