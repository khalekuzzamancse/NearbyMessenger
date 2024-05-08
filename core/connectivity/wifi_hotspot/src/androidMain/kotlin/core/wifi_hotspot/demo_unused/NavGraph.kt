package core.wifi_hotspot.demo_unused

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
 fun NavGraphWifiHotSpot() {
    var showDialog by remember { mutableStateOf(true) }
    val viewModel= remember {
        ServerClient()
    }
    if (showDialog){
        JoinAsDialog(
            onJoinAs = {
                when(it){
                    NetworkRole.Server -> viewModel.startServer()
                    is NetworkRole.Client ->{
                        viewModel.startClient(it.serverIP,it.serverPort)
                    }
                }
                showDialog=false
            },
        )
    }

}

