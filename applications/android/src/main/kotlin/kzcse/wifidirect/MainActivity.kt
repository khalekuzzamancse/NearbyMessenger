package kzcse.wifidirect

import navigation.TechnologyInputDialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.runtime.collectAsState
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import core.notification.StandardNotificationBuilder
import kzcse.wifidirect.deviceInfo.UserNameDialog
import kzcse.wifidirect.deviceInfo.UserNameManager
import kzcse.wifidirect.ui.theme.ConnectivitySamplesNetworkingTheme
import navigation.navgraph.RootNavGraph


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
            if (userNameManager.userName.isEmpty()) {
                viewModel.showNameInputDialoge()
            }
            ConnectivitySamplesNetworkingTheme {
                if (viewModel.showUserNameDialog) {
                    UserNameDialog(userNameManager = userNameManager) {
                        viewModel.onNameInputCompleted()
                        log(userNameManager.userName)
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
                            onNewMessageNotificationRequest = ::createNotification,
                            technology = technology,
                            onExitRequest = ::finish
                        )
                    }
                }
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



