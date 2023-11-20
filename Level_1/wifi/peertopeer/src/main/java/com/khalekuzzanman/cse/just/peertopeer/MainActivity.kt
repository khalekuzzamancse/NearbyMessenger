package com.khalekuzzanman.cse.just.peertopeer

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.runtime.collectAsState
import com.khalekuzzanman.cse.just.peertopeer.data_layer.connectivity.WifiAndBroadcastHandlerInstance
import com.khalekuzzanman.cse.just.peertopeer.ui.WifiDialog
import com.khalekuzzanman.cse.just.peertopeer.ui.theme.ConnectivitySamplesNetworkingTheme
import com.khalekuzzanman.cse.just.peertopeer.ui.ui.navigation.NavGraph


class MainActivity : ComponentActivity() {


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ConnectivitySamplesNetworkingTheme {
                PermissionIfNeeded()
                WifiDialog(WifiAndBroadcastHandlerInstance.wifiAndBroadcastHandler.isWifiEnabled.collectAsState().value) {
                }
                NavGraph()
//               ConversionScreenPreview(
//                   viewModel = ConversionScreenViewModel()
//               )

            }

        }
    }

}

