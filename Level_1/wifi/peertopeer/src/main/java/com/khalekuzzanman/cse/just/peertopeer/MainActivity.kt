package com.khalekuzzanman.cse.just.peertopeer

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.runtime.remember
import com.khalekuzzanman.cse.just.peertopeer.data_layer.connectivity.WifiAndBroadcastHandler
import com.khalekuzzanman.cse.just.peertopeer.ui.theme.ConnectivitySamplesNetworkingTheme
import com.khalekuzzanman.cse.just.peertopeer.ui.ui.chat_screen.ConversionScreenPreview
import com.khalekuzzanman.cse.just.peertopeer.ui.ui.devices_list.NearByDeviceScreen

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
object WifiAndBroadcastHandlerInstance {
    lateinit var wifiAndBroadcastHandler:WifiAndBroadcastHandler
    fun create(context: Context) {
        wifiAndBroadcastHandler = WifiAndBroadcastHandler(context)
    }

}


class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WifiAndBroadcastHandlerInstance.create(this)
        setContent {
            ConnectivitySamplesNetworkingTheme {
                PermissionManage(
                    listOf(
                        android.Manifest.permission.ACCESS_WIFI_STATE,
                        android.Manifest.permission.CHANGE_WIFI_STATE,
                        android.Manifest.permission.CHANGE_NETWORK_STATE,
                        android.Manifest.permission.INTERNET,
                        android.Manifest.permission.ACCESS_NETWORK_STATE,
                        android.Manifest.permission.NEARBY_WIFI_DEVICES,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION,
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                    )
                )
              //  NearByDeviceScreen()
                  ConversionScreenPreview()



            }

        }
    }

}

