package navigation.navgraph

import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import chatui.ConversionScreen
import navigation.MainViewModel
import navigation.WifiDialog
import peers.ui.scanneddevice.NearByDeviceScreen


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun NavGraph(wifiEnabled: Boolean,onExitRequest: () -> Unit = {}) {
    val connectedDeviceId = "connectedDeviceID"
    val chatScreenRoute="Conversation/{$connectedDeviceId}"
    if (!wifiEnabled) {
        WifiDialog(true)
    }
    val navController: NavHostController = rememberNavController()
    val mainViewModel = viewModel { MainViewModel() }
    BackHandler {
        navController.popBackStack()
        if (navController.currentBackStackEntry == null)
            onExitRequest()
    }

    NavHost(
        navController = navController,
        route = "MainGraph",
        startDestination = Destination.ScannedDevice.toString()
    ) {
        composable(route = Destination.ScannedDevice.toString()) {
            NearByDeviceScreen(
                viewModel = mainViewModel.deviceListViewModel,
                onConversionOpen = {
                    println("ConversationLog:$it")
                    /*
                      Right now,we are unable to access the this device address,so use name as device identifier.
                    Caution:If multiple device has the same name with your friend list,then it may causes
                    in consistent,since name is not globally unique
                     */
                    navController.navigate("Conversation/${it.name}")
                },
                onGroupFormed = mainViewModel::onGroupFormed
            )

        }
        composable(
            route =chatScreenRoute,
            arguments = listOf(navArgument(connectedDeviceId) { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString(connectedDeviceId)
            //stop the navigation here....
            ConversionScreen(mainViewModel.createChatViewModel(id ?: "anotherDevice"))
        }

    }
}
