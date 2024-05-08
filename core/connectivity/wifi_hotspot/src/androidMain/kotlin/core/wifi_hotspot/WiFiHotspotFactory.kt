package core.wifi_hotspot

import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.wifi.WifiManager

/**
we need to know the wifi status thought the whole    application at any given time,
that is why we keep a single instance.
- The  instance will created to application classes or within activity ,in order to proper broadcast
 */

object WiFiHotspotFactory {
    lateinit var receiver: WiFiHotspotBroadcastReceiver
    fun initializeReceiver(wifiManager: WifiManager,connectivityManager: ConnectivityManager){
        receiver = WiFiHotspotBroadcastReceiver(wifiManager,connectivityManager)
    }

    val intentFilters = IntentFilter().apply {
        /*
        which is called when scan requests are completed, providing their success/failure status.
        For devices running Android 10 (API level 29) and higher, this broadcast will be sent for any full Wi-Fi scan performed on the device
         by the platform or other apps. Apps can passively listen   to all scan completions on device by using the broadcast
          without issuing a scan of their own.
         */
        addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION)

    }

}