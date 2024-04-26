package kzcse.wifidirect

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.runtime.collectAsState
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import kzcse.wifidirect.ui.theme.ConnectivitySamplesNetworkingTheme
import navigation.navgraph.NavGraph
import wifidirect.Factory


class MainActivity : ComponentActivity() {
    private lateinit var uuidManager: DeviceUUIDManager
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        uuidManager = DeviceUUIDManager(this)
        val deviceUUID = uuidManager.deviceUUID

        setContent {
            ConnectivitySamplesNetworkingTheme {
                PermissionIfNeeded()
                val wifiEnabled =
                    Factory.broadcastNConnectionHandler.isWifiEnabled.collectAsState().value
                NavGraph(wifiEnabled = wifiEnabled,
                    onExitRequest = {
                        finish()
                    })
            }

        }
    }

}

