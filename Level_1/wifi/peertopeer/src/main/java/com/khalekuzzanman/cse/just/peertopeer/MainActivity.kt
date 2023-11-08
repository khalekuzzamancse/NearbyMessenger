package com.khalekuzzanman.cse.just.peertopeer

import PermissionManage
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.khalekuzzanman.cse.just.peertopeer.ui.theme.ConnectivitySamplesNetworkingTheme
import com.khalekuzzanman.cse.just.peertopeer.ui.ui.NearByDeviceScreen

class MainActivity : ComponentActivity() {


    @OptIn(ExperimentalPermissionsApi::class)
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ConnectivitySamplesNetworkingTheme {
                PermissionManage(
                    listOf(
                        "android.permission.ACCESS_WIFI_STATE",
                        "android.permission.CHANGE_WIFI_STATE",
                        "android.permission.CHANGE_NETWORK_STATE",
                        "android.permission.INTERNET",
                        "android.permission.ACCESS_NETWORK_STATE",
                        "android.permission.NEARBY_WIFI_DEVICES",
                        "android.permission.ACCESS_COARSE_LOCATION",
                        "android.permission.ACCESS_FINE_LOCATION"
                    )
                )
              NearByDeviceScreen()

            }

        }
    }

}

