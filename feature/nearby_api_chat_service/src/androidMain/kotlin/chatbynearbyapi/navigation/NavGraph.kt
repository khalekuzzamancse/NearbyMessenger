package chatbynearbyapi.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import chatbynearbyapi.chat.ConversationScreen
import chatbynearbyapi.chat.DataCommunicatorImpl
import chatbynearbyapi.devices.DeviceListViewModel
import chatui.viewmodel.ChatViewModel

@Composable
fun NearByAPIChatServiceNavGraph(
    thisDeviceName: String,
    onNewMessageNotificationRequest: (sender: String) -> Unit,
    onExitRequest: () -> Unit,
) {
    var role by remember { mutableStateOf<NetworkRole?>(null) }

    JoinAsDialog { role = it }
    role?.let { endpointRole ->
        _NavGraph(
            thisDeviceName,
            endpointRole,
            onNewMessageNotificationRequest,
            onExitRequest
        )
    }


}

@Suppress("ComposableNaming")
@Composable
private fun _NavGraph(
    thisDeviceName: String,
    role: NetworkRole,
    onNewMessageNotificationRequest: (sender: String) -> Unit,
    onExitRequest: () -> Unit,
) {

    val navController: NavHostController = rememberNavController()
    val context = LocalContext.current
    val deviceListViewModel: DeviceListViewModel =
        remember { DeviceListViewModel(context, thisDeviceName, role) }
    val chatViewModel= remember {
        ChatViewModel(
            DataCommunicatorImpl(thisDeviceName, deviceListViewModel),
            thisDeviceName
        )
    }

    BackHandler {
        navController.popBackStack()
        if (navController.currentBackStackEntry == null)
        {
            onExitRequest()
        }
    }

    NavHost(
        navController = navController,
        route = "MainGraph",
        startDestination = Destination.Home.toString()
    ) {
        composable(route = Destination.Home.toString()) {
            HomeScreen(
                thisDeviceName = thisDeviceName,
                deviceListViewModel =deviceListViewModel,
                chatViewModel = chatViewModel,
                chatScreenTitle = "Group Chat",
                onGroupConversationRequest = {
                    navController.navigate(Destination.Conversation.toString())
                }

            )

        }
        composable(
            route = Destination.Conversation.toString(),
        ) {
            ConversationScreen(
                chatViewModel =chatViewModel
            )
        }

    }
}
