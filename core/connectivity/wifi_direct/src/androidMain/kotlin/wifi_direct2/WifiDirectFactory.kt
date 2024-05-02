package wifi_direct2

import android.content.Context
import android.net.wifi.p2p.WifiP2pManager

/**
we need to know the wifi status thought the whole    application at any given time,
that is why we keep a single instance.
- The  instance will created to application classes or within activity ,in order to proper broadcast
 */

 object WifiDirectFactory {
    lateinit var broadcastReceiver: WifiDirectBroadcastReceiver

    /**
     * - Create the instance in the application class
     */
    fun setBroadcastReceiver(
        manager: WifiP2pManager,
        channel: WifiP2pManager.Channel,
    ){
        broadcastReceiver = WifiDirectBroadcastReceiver(manager,channel)
    }

}