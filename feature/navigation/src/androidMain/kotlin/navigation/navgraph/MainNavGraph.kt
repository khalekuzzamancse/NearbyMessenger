package navigation.navgraph

import Technology
import TechnologyInputDialog
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import chatbynearbyapi.navigation.NearByAPIChatServiceNavGraph
import chatbywifidirect.navigation.WifiDirectChatServiceNavGraph
import navigation.theme.Theme

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun RootNavGraph(
    thisDeviceUserName: String,
    wifiEnabled: Boolean,
    onNewMessageNotificationRequest: (sender: String) -> Unit,
    onExitRequest: () -> Unit,
) {
    Theme {
        _NavGraph(
            thisDeviceUserName = thisDeviceUserName,
            wifiEnabled = wifiEnabled,
            onNewMessageNotificationRequest = onNewMessageNotificationRequest,
            onExitRequest = onExitRequest
        )
    }

}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Suppress("ComposableNaming")
@Composable
private fun _NavGraph(
    thisDeviceUserName: String,
    wifiEnabled: Boolean,
    onNewMessageNotificationRequest: (sender: String) -> Unit,
    onExitRequest: () -> Unit,
) {

    val navController = rememberNavController()
    BackHandler {
        navController.popBackStack()
        if (navController.currentBackStackEntry == null){

        }
          //  onExitRequest()
    }

    NavHost(
        navController = navController,
        startDestination = Destination.Home.toString()
    ) {
        composable(route = Destination.Home.toString()) {
            TechnologyInputDialog(
                onTechnologySelected = { technology ->
                    when (technology) {
                        Technology.WifiDirect -> {

                            navController.navigate(Destination.ChatByWifiDirect.toString())
                        }

                        Technology.NearByAPI -> {
                            navController.navigate(Destination.ChatByNearByAPI.toString())
                        }

                        else -> {
                            navController.navigate(Destination.UnderConstruction.toString())
                        }
                    }
                },
            )
        }
        composable(route = Destination.ChatByWifiDirect.toString()) {
            WifiDirectChatServiceNavGraph(
                thisDeviceUserName = thisDeviceUserName,
                wifiEnabled = wifiEnabled,
                onNewMessageNotificationRequest = onNewMessageNotificationRequest,
                onExitRequest = onExitRequest
            )
        }
        composable(route = Destination.ChatByNearByAPI.toString()) {
            NearByAPIChatServiceNavGraph(
                thisDeviceName = thisDeviceUserName,
                onExitRequest = onExitRequest,
                onNewMessageNotificationRequest = onNewMessageNotificationRequest
            )
        }
        composable(route = Destination.UnderConstruction.toString()) {
            _NotImplemented()
        }
    }

}

@Composable
private fun _NotImplemented() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Under Construction")
    }

}
