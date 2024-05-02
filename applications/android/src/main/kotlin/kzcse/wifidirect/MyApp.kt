package kzcse.wifidirect

import android.app.Application
import android.content.Context
import android.net.wifi.p2p.WifiP2pManager
import android.util.Log
import wifi_direct2.WifiDirectFactory


class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()
        val manager = getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
        val channel = manager.initialize(this, mainLooper, null)
        WifiDirectFactory.setBroadcastReceiver(manager, channel)


    }


    override fun onTerminate() {
        //  WifiDirectFactory.broadcastNConnectionHandler.disconnectAll()
        super.onTerminate()
    }

    @Suppress("Unused")
    private fun log(message: String, methodName: String? = null) {
        val tag = "${this@MyApp::class.simpleName}Log:"
        val method = if (methodName == null) "" else "$methodName()'s "
        val msg = "$method:-> $message"
        Log.d(tag, msg)
    }

}