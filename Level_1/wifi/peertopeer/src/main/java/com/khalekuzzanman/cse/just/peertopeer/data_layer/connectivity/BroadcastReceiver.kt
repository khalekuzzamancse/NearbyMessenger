package com.khalekuzzanman.cse.just.peertopeer.data_layer.connectivity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/*
This is re usable
 */

class WifiDirectBroadcastReceiver(
    private val interestedActions: List<BroadcastReceiverAction>
) : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val receivedAction = intent.action
        if (receivedAction != null) {
            notifyActionOccurred(receivedAction)
        }
    }

    private fun notifyActionOccurred(occurredAction: String) {
        interestedActions.forEach { interestedAction ->
            if (interestedAction.action == occurredAction) {
                interestedAction.handler()
            }
        }
    }
}

class BroadcastReceiverAction(
    val action: String,
    val handler: () -> Unit = {}
)