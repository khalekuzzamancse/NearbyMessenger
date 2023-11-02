package com.khalekuzzanman.cse.just.peertopeer

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import com.khalekuzzanman.cse.just.peertopeer.data_layer.WifiAndBroadcastHandler
import com.khalekuzzanman.cse.just.peertopeer.ui.theme.ConnectivitySamplesNetworkingTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ConnectivitySamplesNetworkingTheme {

            }

        }
    }

}

