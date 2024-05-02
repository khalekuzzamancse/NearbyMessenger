package wifidirect.broadcast

import android.content.Intent

internal class BroadcastReceiverAction(
    val action: String,
    val handler: (Intent) -> Unit = {}
)