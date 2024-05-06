package kzcse.wifidirect

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import core.notification.StandardNotificationBuilder
import kzcse.wifidirect.deviceInfo.UserNameDialog
import kzcse.wifidirect.deviceInfo.UserNameManager
import kzcse.wifidirect.ui.theme.ConnectivitySamplesNetworkingTheme
import navigation.navgraph.RootNavGraph
import navigation.tech_select_dialouge.TechInputRoute
import navigation.tech_select_dialouge.Technology


class MainActivity : ComponentActivity() {
    private lateinit var userNameManager: UserNameManager


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        userNameManager = UserNameManager(this)



        setContent {
            val viewModel = viewModel {
                AppViewModel()
            }
            if (userNameManager.userName.isEmpty())
                viewModel.showNameInputDialog()

            ConnectivitySamplesNetworkingTheme {
                LaunchedEffect(Unit) {
                    MyApp.createBluetoothController(this@MainActivity)
                }
                _RootNavGraph(
                    viewModel = viewModel,
                    userNameManager = userNameManager,
                    onNewMessageNotificationRequest = ::createNotification,
                    onExitRequest = ::finish,
                    appContext = applicationContext
                )
            }
            PermissionIfNeeded()
        }
    }


    private fun createNotification(senderName: String) {
        StandardNotificationBuilder(this@MainActivity)
            .notify(title = "New Message", message = "from $senderName")
    }


    /** register the BroadcastReceiver with the intent values to be matched  */


    @Suppress("Unused")
    private fun log(message: String, methodName: String? = null) {
        val tag = "${this@MainActivity::class.simpleName}Log:"
        val method = if (methodName == null) "" else "$methodName()'s "
        val msg = "$method:-> $message"
        Log.d(tag, msg)
    }


}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Suppress("ComposableNaming")
@Composable
private fun _RootNavGraph(
    appContext: Context,
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

            TechInputRoute(
                onTechSelected = { technology ->
                    println("TechnologySelected: $technology")
                    when (technology) {
                        //register for wifi direct technology if use selected wifi direct
                        Technology.WifiDirect -> MyApp.registerForWifiDirectBroadcast(appContext)
                        Technology.WifiHotspot -> MyApp.registerForWifiHotspotBroadcast(appContext)

                        else -> {}
                    }
                    viewModel.onTechSelected(technology)
                }
            )
        }
        viewModel.selectedTech?.let { technology ->
            RootNavGraph(
                thisDeviceUserName = userNameManager.userName,
                wifiEnabled = viewModel.wifiEnabled.collectAsState().value,
                onNewMessageNotificationRequest = onNewMessageNotificationRequest,
                technology = technology,
                onExitRequest = onExitRequest,
                onTechSelectRequest = viewModel::onTechAgainTechSelectRequest
            )
        }
    }

}



