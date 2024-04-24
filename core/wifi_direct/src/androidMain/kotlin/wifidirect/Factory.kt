package wifidirect

import android.content.Context
import android.net.wifi.WifiManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import wifidirect.broadcast.WifiDirectBroadcastManager
import wifidirect.connection.ConnectionManager

/*
we need to know the wifi status thought the whole    application at any given time,
that is why we keep a single instance
 */
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
object Factory {
    lateinit var broadcastNConnectionHandler: BroadcastNConnectionHandler
    fun create(context: Context) {
        broadcastNConnectionHandler = BroadcastNConnectionHandler(context)
    }

}