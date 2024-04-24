package navigation

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
import peers.ui.scanneddevice.NearByDeviceScreen


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun NavGraph(wifiEnabled: Boolean) {
    if (!wifiEnabled) {
        WifiDialog(true)
    }
    val navController: NavHostController = rememberNavController()
    val mainViewModel = viewModel { MainViewModel() }

    BackHandler {

    }

    NavHost(
        navController = navController,
        route = "MainGraph",
        startDestination = "DeviceScreen"
    ) {
        composable(route = "DeviceScreen") {
            NearByDeviceScreen(
                viewModel = mainViewModel.deviceListViewModel,
                onConversionOpen = {
                    navController.navigate("ConversionScreen")
                },
                onGroupFormed = mainViewModel::onGroupFormed
            )
        }
        composable(route = "ConversionScreen") {
            ConversionScreen(mainViewModel.chatViewModel)
        }


    }
}