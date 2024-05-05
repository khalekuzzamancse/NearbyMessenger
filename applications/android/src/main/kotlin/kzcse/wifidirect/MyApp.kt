package kzcse.wifidirect

import android.app.Application
import android.bluetooth.BluetoothManager
import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.net.wifi.p2p.WifiP2pManager
import android.util.Log
import blueetooth.BluetoothFactory
import core.wifi_hotspot.WiFiHotspotFactory
import wifi_direct2.WifiDirectFactory
import wifi_direct2.WifiDirectIntentFilters


class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()
    }

    /**
     * - only register if user select the Wifi Direct technology for chat
     */
    companion object {
        fun registerForWifiDirectBroadcast(context: Context) {
            val manager = context.getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
            val channel = manager.initialize(context, context.mainLooper, null)
            WifiDirectFactory.setBroadcastReceiver(manager, channel)
            context.registerReceiver(
                WifiDirectFactory.broadcastReceiver, WifiDirectIntentFilters.filters,
                RECEIVER_NOT_EXPORTED //TODO:Refactor later,can causes bug in android >10
            )
        }

        fun registerForWifiHotspotBroadcast(context: Context) {
            val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            WiFiHotspotFactory.initializeReceiver(wifiManager,connectivityManager)
            context.registerReceiver(
                WiFiHotspotFactory.receiver, WiFiHotspotFactory.intentFilters,
                RECEIVER_EXPORTED
            ) //TODO:Refactor later,can causes bug in android >10
        }
        fun createBluetoothController(context: Context) {
            val bluetoothManager: BluetoothManager = context.getSystemService(BluetoothManager::class.java)
            BluetoothFactory.createBluetoothController(bluetoothManager)
        }
    }


    override fun onTerminate() {
        super.onTerminate()
        try {
            //if broadcast receiver has created
            unregisterReceiver(WifiDirectFactory.broadcastReceiver)
        } catch (_: Exception) {

        }

    }

    @Suppress("Unused")
    private fun log(message: String, methodName: String? = null) {
        val tag = "${this@MyApp::class.simpleName}Log:"
        val method = if (methodName == null) "" else "$methodName()'s "
        val msg = "$method:-> $message"
        Log.d(tag, msg)
    }

}