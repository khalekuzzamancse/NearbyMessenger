package kzcse.wifidirect

import android.app.Application
import android.os.Build
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

}