package kzcse.wifidirect

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.runtime.collectAsState
import kzcse.wifidirect.ui.theme.ConnectivitySamplesNetworkingTheme
import navigation.NavGraph
import wifidirect.Factory


class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ConnectivitySamplesNetworkingTheme {
                PermissionIfNeeded()
               val wifiEnabled= Factory.broadcastNConnectionHandler.isWifiEnabled.collectAsState().value
                NavGraph(wifiEnabled = wifiEnabled)
            }

        }
    }

}

