package wifidirect

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi

/**
we need to know the wifi status thought the whole    application at any given time,
that is why we keep a single instance.
- The  instance will created to application classes,in order to proper broadcast
 */
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
 object WifiDirectFactory {
    lateinit var broadcastNConnectionHandler: BroadcastNConnectionHandler

    /**
     * - Create the instance in the application class
     */
    fun create(context: Context) {
        broadcastNConnectionHandler = BroadcastNConnectionHandler(context)
    }

}