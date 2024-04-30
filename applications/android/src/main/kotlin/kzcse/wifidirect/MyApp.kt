package kzcse.wifidirect

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import wifidirect.Factory


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