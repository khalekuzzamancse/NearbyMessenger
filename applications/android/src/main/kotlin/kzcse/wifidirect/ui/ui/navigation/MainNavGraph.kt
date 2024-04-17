package kzcse.wifidirect.ui.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kzcse.wifidirect.ui.ui.chat_screen.ConversationRoute
import kzcse.wifidirect.ui.ui.chat_screen.ConversionScreenViewModel
import peers.ui.scanneddevice.NearByDeviceScreen


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun NavGraph() {
    val resolver = LocalContext.current.contentResolver
    val navController: NavHostController = rememberNavController()
    val viewModel = remember {
        ConversionScreenViewModel(
            resolver = resolver
        )
    }
    NavHost(
        navController = navController,
        route = "MainGraph",
        startDestination = "DeviceScreen"
    ) {
        composable(route = "DeviceScreen") {
            NearByDeviceScreen(
                onConversionOpen = {
                    navController.navigate("ConversionScreen")
                }
            )

        }
        composable(route = "ConversionScreen") {
            ConversationRoute(
                viewModel = viewModel
            )

        }

    }
}