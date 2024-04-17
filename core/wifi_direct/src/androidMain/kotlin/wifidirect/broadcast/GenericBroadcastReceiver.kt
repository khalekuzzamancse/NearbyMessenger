package wifidirect.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/*
This is re usable
 */

internal class GenericBroadcastReceiver(
    private val interestedActions: List<BroadcastReceiverAction>
) : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val receivedAction = intent.action
        if (receivedAction != null) {
            notifyActionOccurred(receivedAction,intent)
        }
    }

    private fun notifyActionOccurred(occurredAction: String,intent: Intent) {
        interestedActions.forEach { interestedAction ->
            if (interestedAction.action == occurredAction) {
                interestedAction.handler(intent)
            }
        }
    }
}

class BroadcastReceiverAction(
    val action: String,
    val handler: (Intent) -> Unit = {}
)
