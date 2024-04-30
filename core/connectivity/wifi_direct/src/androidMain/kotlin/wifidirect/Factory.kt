package wifidirect

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi

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