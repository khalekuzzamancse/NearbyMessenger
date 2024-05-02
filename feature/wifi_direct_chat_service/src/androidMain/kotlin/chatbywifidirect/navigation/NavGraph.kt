package chatbywifidirect.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import chatbywifidirect.WifiDialog
import chatbywifidirect.chat.ConversationScreen
import chatbywifidirect.navigation.route.Destination
import chatbywifidirect.navigation.route.HomeScreen
import chatbywifidirect.navigation.route.MainViewModel

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Suppress("ComposableNaming")
@Composable
 fun WifiDirectChatServiceNavGraph(
    thisDeviceUserName:String,
    wifiEnabled: Boolean,
    onNewMessageNotificationRequest:(sender:String)->Unit,
    onExitRequest: () -> Unit,
) {
    if (!wifiEnabled) {
        WifiDialog()
    }
    val navController: NavHostController = rememberNavController()
    val mainViewModel = viewModel { MainViewModel(thisDeviceUserName) }

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

