package kzcse.wifidirect.ui

import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import chatui.Conversions
import chatui.MessageFieldController
import kotlinx.coroutines.launch
import kzcse.wifidirect.JoinAsDialog
import peers.ui.scanneddevice.DeviceListViewModel
import peers.ui.scanneddevice.NearByDeviceScreen


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun NavGraph() {
    val navController: NavHostController = rememberNavController()
    val dataCommunicator = remember { DataCommunicator() }
    val chatViewModel = remember { ChatViewModel(dataCommunicator) }
    BackHandler {

    }
    val scope = rememberCoroutineScope()
    NavHost(
        navController = navController,
        route = "MainGraph",
        startDestination = "DeviceScreen"
    ) {

        composable(route = "DeviceScreen") {
            val deviceListViewModel = remember {
                DeviceListViewModel()
            }
            NearByDeviceScreen(
                viewModel = deviceListViewModel,
                onConversionOpen = {
                    navController.navigate("ConversionScreen")
                },
                onGroupFormed = dataCommunicator::onGroupFormed
            )
        }
        composable(route = "ConversionScreen") {
            Conversions(
                conversations = chatViewModel.conversations.collectAsState().value,
                controller = chatViewModel.controller,
                onSendButtonClick = {
                    scope.launch {
                        chatViewModel.sendMessage()
                    }

                },
                onAttachmentClick = {
                },
                onSpeechToTextRequest = {},
                navigationIcon = null
            )
        }
        composable(route = "Join") {
            var showDialog by remember { mutableStateOf(true) }
            if (showDialog) {
                JoinAsDialog(
                    onJoinAs = {
                        showDialog = false
                    },
                )
            } else {
                navController.navigate("ConversionScreen")
            }


        }

    }
}