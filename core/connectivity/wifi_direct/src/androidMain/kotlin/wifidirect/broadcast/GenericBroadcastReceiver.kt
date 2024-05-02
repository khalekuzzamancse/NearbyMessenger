package wifidirect.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

/**
 * - A generic broadcast receiver simply requires you to provide a list of relevant intents,
 * and it will handle the broadcasting process for you
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
    @Suppress("Unused")
    private fun log(message: String, methodName: String? = null) {
        val tag = "${this@GenericBroadcastReceiver::class.simpleName}Log:"
        val method = if (methodName == null) "" else "$methodName()'s "
        val msg = "$method:-> $message"
        Log.d(tag, msg)
    }
}


