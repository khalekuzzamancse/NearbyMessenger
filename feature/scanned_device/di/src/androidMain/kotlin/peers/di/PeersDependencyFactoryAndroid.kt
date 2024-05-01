package peers.di

import android.os.Build
import androidx.annotation.RequiresApi
import peers.data.WifiDirectController
import wifidirect.misc.ConnectionController

object PeersDependencyFactoryAndroid {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun createConnectionController(): ConnectionController {
        return WifiDirectController()
    }
}