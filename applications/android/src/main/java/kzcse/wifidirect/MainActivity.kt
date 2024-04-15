package kzcse.wifidirect

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.runtime.collectAsState
import kzcse.wifidirect.data_layer.connectivity.WifiAndBroadcastHandlerInstance
import kzcse.wifidirect.ui.WifiDialog
import kzcse.wifidirect.ui.theme.ConnectivitySamplesNetworkingTheme
import kzcse.wifidirect.ui.ui.navigation.NavGraph


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

