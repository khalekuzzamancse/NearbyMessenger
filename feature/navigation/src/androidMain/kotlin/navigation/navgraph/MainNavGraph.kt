package navigation.navgraph

import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import chatui.ConversionScreen
import navigation.MainViewModel
import navigation.WifiDialog
import peers.ui.scanneddevice.NearByDeviceScreen


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun NavGraph(wifiEnabled: Boolean,onExitRequest:()->Unit={}) {
    if (!wifiEnabled) {
        WifiDialog(true)
    }
    val navController: NavHostController = rememberNavController()
    val mainViewModel = viewModel { MainViewModel() }
    BackHandler {
       navController.popBackStack()
        if (navController.currentBackStackEntry==null)
            onExitRequest()
    }

    NavHost(
        navController = navController,
        route = "MainGraph",
        startDestination = Destination.ScannedDevice.toString()
    ) {
        composable(route =Destination.ScannedDevice.toString()) {
            NearByDeviceScreen(
                viewModel = mainViewModel.deviceListViewModel,
                onConversionOpen = {
                    println("ConversationLog:$it")
                    navController.navigate(Destination.Conversation.toString())
                },
                onGroupFormed = mainViewModel::onGroupFormed
            )

        }
        composable(route = Destination.Conversation.toString()) {
            ConversionScreen(mainViewModel.chatViewModel)
        }

    }
}
