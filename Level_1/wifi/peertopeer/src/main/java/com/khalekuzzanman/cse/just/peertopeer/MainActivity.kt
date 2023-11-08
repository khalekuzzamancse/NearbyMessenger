package com.khalekuzzanman.cse.just.peertopeer

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
                NearByDeviceScreen()

            }

        }
    }

}

