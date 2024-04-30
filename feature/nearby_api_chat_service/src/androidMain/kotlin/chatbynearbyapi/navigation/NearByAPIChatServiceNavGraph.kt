package chatbynearbyapi.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import chatbynearbyapi.devices.DeviceListViewModel
import chatbynearbyapi.devices.NearByDeviceScreen

@Suppress("ComposableNaming")
@Composable
fun NearByAPIChatServiceNavGraph(
    thisDeviceName: String,
    isAdvertiser: Boolean,
    onNewMessageNotificationRequest: (sender: String) -> Unit,
    onExitRequest: () -> Unit,
) {

    val navController: NavHostController = rememberNavController()
    val context = LocalContext.current
    val viewModel: DeviceListViewModel =
        remember { DeviceListViewModel(context, thisDeviceName, isAdvertiser) }

//    BackHandler {
//        navController.popBackStack()
//        if (navController.currentBackStackEntry == null)
//            onExitRequest()
//    }

    NavHost(
        navController = navController,
        route = "MainGraph",
        startDestination = Destination.Home.toString()
    ) {
        composable(route = Destination.Home.toString()) {
                NearByDeviceScreen(
                    modifier = Modifier,
                    viewModel=viewModel,
                    thisDeviceName = thisDeviceName,
                    onConversionOpen = {},
                    onGroupConversationRequest = {}
                )


        }

    }
}
