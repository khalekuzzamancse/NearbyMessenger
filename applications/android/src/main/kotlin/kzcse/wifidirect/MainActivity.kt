package kzcse.wifidirect

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import chatbynearbyapi.navigation.NearByAPIChatServiceNavGraph
import core.notification.StandardNotificationBuilder
import kzcse.wifidirect.deviceInfo.UserNameManager
import kzcse.wifidirect.ui.theme.ConnectivitySamplesNetworkingTheme


class MainActivity : ComponentActivity() {
    private lateinit var userNameManager: UserNameManager
    private var showDialog by mutableStateOf(false)
    private var joinAsAdvertiser by mutableStateOf<Boolean?>(null)
    private var deviceName="Tab"

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        userNameManager = UserNameManager(this)
        showDialog = userNameManager.userName.isEmpty()

        setContent {
            ConnectivitySamplesNetworkingTheme {
                joinAsAdvertiser?.let {
                    NearByAPIChatServiceNavGraph(
                        thisDeviceName = deviceName,
                        isAdvertiser = it,
                        onExitRequest = {},
                        onNewMessageNotificationRequest = {}
                    )
                }
                if (joinAsAdvertiser==null){
                    Row {
                        Button(onClick = {
                            joinAsAdvertiser = true
                            deviceName="Tab"
                        }) {
                            Text(text = "Adv")
                        }
                        Button(onClick = {
                            joinAsAdvertiser = false
                            deviceName="Phone"
                        }) {
                            Text(text = "Dis")
                        }

                    }
                }


//                if (showDialog) {
//                    UserNameDialog(userNameManager = userNameManager) {
//                        showDialog = false
//                        log(userNameManager.userName)
//                    }
//                } else {
////                    val wifiEnabled = Factory.broadcastNConnectionHandler.isWifiEnabled.collectAsState().value
//                    val wifiEnabled = true
//                    NavGraph(
//                        thisDeviceUserName = userNameManager.userName,
//                        wifiEnabled = wifiEnabled,
//                        onNewMessageNotificationRequest = ::createNotification,
//                        onExitRequest = ::finish
//                    )
//
//                }
            }
            PermissionIfNeeded()
        }
    }

    private fun createNotification(senderName: String) {
        StandardNotificationBuilder(this@MainActivity)
            .notify(title = "New Message", message = "from $senderName")
    }

    @Suppress("Unused")
    private fun log(message: String, methodName: String? = null) {
        val tag = "${this@MainActivity::class.simpleName}Log:"
        val method = if (methodName == null) "" else "$methodName()'s "
        val msg = "$method:-> $message"
        Log.d(tag, msg)
    }
}



