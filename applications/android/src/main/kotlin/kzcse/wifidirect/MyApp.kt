package kzcse.wifidirect

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.LinkAddress
import android.net.LinkProperties
import android.net.wifi.WifiManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import navigation.DataCommunicatorImpl
import wifidirect.Factory
import java.net.InetAddress
import java.nio.ByteBuffer
import java.nio.ByteOrder


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Factory.create(this)
        Factory.broadcastNConnectionHandler.registerBroadcast()
        Factory.broadcastNConnectionHandler.updateConnectedDeviceInfo()

    }




    override fun onTerminate() {
        Factory.broadcastNConnectionHandler.disconnectAll()
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