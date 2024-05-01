package kzcse.wifidirect

import android.content.Context
import android.net.wifi.p2p.WifiP2pManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import core.notification.StandardNotificationBuilder
import kotlinx.coroutines.launch
import kzcse.wifidirect.deviceInfo.UserNameDialog
import kzcse.wifidirect.deviceInfo.UserNameManager
import kzcse.wifidirect.ui.theme.ConnectivitySamplesNetworkingTheme
import navigation.TechnologyInputDialog
import navigation.navgraph.RootNavGraph
import wifi_direct2.WifiDirectBroadcastReceiver
import wifi_direct2.WifiDirectIntentFilters


class MainActivity : ComponentActivity() {
    private lateinit var userNameManager: UserNameManager
    private lateinit var receiver: WifiDirectBroadcastReceiver

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        userNameManager = UserNameManager(this)
        receiver = createWifiDirectBroadCastReceiver()

        setContent {
            val scope = rememberCoroutineScope()

            LaunchedEffect(Unit) {
                receiver.peers.collect {
                    log("Peers:$it")
                }
            }
            LaunchedEffect(Unit) {
                receiver.connectionInfo.collect {
                    log("ConnectionIfo:$it")
                }
            }
//            val viewModel = viewModel {
//                AppViewModel()
//            }
//            if (userNameManager.userName.isEmpty()) {
//                viewModel.showNameInputDialoge()
//            }
            ConnectivitySamplesNetworkingTheme {

                Column {
                    Button(onClick = {
                        scope.launch {
                            val result = receiver.startDiscovery()
                            log("Scan:$result")
                        }
                    }) {
                        Text(text = "Scan")
                    }
                    receiver.peers.collectAsState(initial = emptyList()).value.forEach {
                        Text(
                            text = it.deviceName,
                            modifier = Modifier.clickable {
                                scope.launch {
                                    val result =  receiver.initiateConnection(it.deviceAddress)
                                    log("ConnectionInitiated:$result")
                                }

                            },
                        )
                    }
                }


//                _RootNavGraph(
//                    viewModel = viewModel,
//                    userNameManager = userNameManager,
//                    onNewMessageNotificationRequest = ::createNotification,
//                    onExitRequest = ::finish
//                )
            }
            PermissionIfNeeded()
        }
    }

    private fun createNotification(senderName: String) {
        StandardNotificationBuilder(this@MainActivity)
            .notify(title = "New Message", message = "from $senderName")
    }


    /** register the BroadcastReceiver with the intent values to be matched  */
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    public override fun onResume() {
        super.onResume()
//        receiver = createWifiDirectBroadCastReceiver()
        registerReceiver(receiver, WifiDirectIntentFilters.filters, RECEIVER_NOT_EXPORTED)
    }

    public override fun onPause() {
        super.onPause()
        // unregisterReceiver(receiver)
    }

    @Suppress("Unused")
    private fun log(message: String, methodName: String? = null) {
        val tag = "${this@MainActivity::class.simpleName}Log:"
        val method = if (methodName == null) "" else "$methodName()'s "
        val msg = "$method:-> $message"
        Log.d(tag, msg)
    }


    private fun createWifiDirectBroadCastReceiver(): WifiDirectBroadcastReceiver {
        val manager = getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
        val channel = manager.initialize(this, mainLooper, null)
        return WifiDirectBroadcastReceiver(manager, channel)
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Suppress("ComposableNaming")
@Composable
private fun _RootNavGraph(
    viewModel: AppViewModel,
    userNameManager: UserNameManager,
    onNewMessageNotificationRequest: (String) -> Unit,
    onExitRequest: () -> Unit,

    ) {
    if (viewModel.showUserNameDialog) {
        UserNameDialog(userNameManager = userNameManager) {
            viewModel.onNameInputCompleted()
        }
    } else {
        val technologyNotSelected = viewModel.selectedTech == null
        if (technologyNotSelected) {
            TechnologyInputDialog {
                viewModel.onTechSelected(it)
            }
        }
        viewModel.selectedTech?.let { technology ->
            RootNavGraph(
                thisDeviceUserName = userNameManager.userName,
                wifiEnabled = viewModel.wifiEnabled.collectAsState().value,
                onNewMessageNotificationRequest = onNewMessageNotificationRequest,
                technology = technology,
                onExitRequest = onExitRequest
            )
        }
    }

}



