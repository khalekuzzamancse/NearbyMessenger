package com.khalekuzzanman.cse.just.peertopeer

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import com.khalekuzzanman.cse.just.peertopeer.data_layer.connectivity.WifiAndBroadcastHandler
import com.khalekuzzanman.cse.just.peertopeer.data_layer.connectivity.WifiAndBroadcastHandlerInstance

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()
        val wifiAndBroadcastHandlerInstance = WifiAndBroadcastHandlerInstance.create(this)
    }

}