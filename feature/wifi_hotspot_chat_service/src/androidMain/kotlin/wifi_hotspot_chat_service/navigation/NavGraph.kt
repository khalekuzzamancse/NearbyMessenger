package wifi_hotspot_chat_service.navigation

import android.os.Build
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import core.wifi_hotspot.WiFiHotspotFactory
import kotlinx.coroutines.launch
import wifi_hotspot_chat_service.chat.ConversationScreen
import wifi_hotspot_chat_service.join_as_dialogue.ConfirmHotspotOwnerDialog
import wifi_hotspot_chat_service.misc.WifiDialog
import wifi_hotspot_chat_service.navigation.route.Destination
import wifi_hotspot_chat_service.navigation.route.HomeScreen
import wifi_hotspot_chat_service.navigation.route.MainViewModel


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Suppress("ComposableNaming")
@Composable
fun WifiHotspotChatServiceNavGraph(
    thisDeviceUserName:String,
    wifiEnabled: Boolean,
    onNewMessageNotificationRequest:(sender:String)->Unit,
    onExitRequest: () -> Unit,
){
    val mainViewModel = viewModel { MainViewModel(thisDeviceUserName) }
    if (mainViewModel.showJoinAsDialog.collectAsState().value){
        ConfirmHotspotOwnerDialog(
            onNavigationRequest = mainViewModel::onNavigateRequest
        )

    }else{
        _WifiHotspotChatServiceNavGraph(
            mainViewModel = mainViewModel,
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
 private fun _WifiHotspotChatServiceNavGraph(
    mainViewModel: MainViewModel,
    thisDeviceUserName:String,
    wifiEnabled: Boolean,
    onNewMessageNotificationRequest:(sender:String)->Unit,
    onExitRequest: () -> Unit,
) {
    if (!wifiEnabled) {
        WifiDialog()
    }
    val navController: NavHostController = rememberNavController()


    LaunchedEffect(Unit){
        mainViewModel.newMessage.collect{newMessage->
            if (newMessage!=null){
                onNewMessageNotificationRequest(newMessage.senderName)
            }
        }
    }



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
            HomeScreen(
                thisDeviceName = thisDeviceUserName,
                deviceListViewModel =mainViewModel.deviceListViewModel,
                chatViewModel = mainViewModel.createChatViewModel(),
                chatScreenTitle = "Group Chat",
                wifiEnabled=wifiEnabled,
                onGroupFormed = mainViewModel::onGroupFormed,
                onGroupConversationRequest = {
                    navController.navigate(Destination.Conversation.toString())
                }

            )

        }
        composable(
            route = Destination.Conversation.toString(),
        ) {
            ConversationScreen(
                chatViewModel= mainViewModel.createChatViewModel()
            )
        }
    }
}

