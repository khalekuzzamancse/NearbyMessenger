package navigation.navgraph

import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import chatui.conversations.ConversionRoute
import navigation.MainViewModel
import navigation.WifiDialog


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun NavGraph(
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

