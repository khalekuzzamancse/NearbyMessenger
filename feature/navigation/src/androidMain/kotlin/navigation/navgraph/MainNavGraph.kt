package navigation.navgraph

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import chatbynearbyapi.navigation.NearByAPIChatServiceNavGraph
import chatbywifidirect.navigation.WifiDirectChatServiceNavGraph
import navigation.tech_select_dialouge.Technology
import navigation.tech_select_dialouge.TechInputRoute
import navigation.theme.Theme
import wifi_hotspot_chat_service.navigation.WifiHotspotChatServiceNavGraph


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun RootNavGraph(
    thisDeviceUserName: String,
    wifiEnabled: Boolean,
    technology: Technology,
    onNewMessageNotificationRequest: (sender: String) -> Unit,
    onTechSelectRequest: () -> Unit,
    onExitRequest: () -> Unit,
) {
    Theme {
        _NavGraph(
            thisDeviceUserName = thisDeviceUserName,
            wifiEnabled = wifiEnabled,
            technology=technology,
            onNewMessageNotificationRequest = onNewMessageNotificationRequest,
            onExitRequest = onExitRequest,
            onTechSelectRequest = onTechSelectRequest
        )
    }

}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Suppress("ComposableNaming")
@Composable
private fun _NavGraph(
    thisDeviceUserName: String,
    wifiEnabled: Boolean,
    technology: Technology,
    onNewMessageNotificationRequest: (sender: String) -> Unit,
    onTechSelectRequest: () -> Unit,
    onExitRequest: () -> Unit,

) {
    when(technology){
        Technology.NearByAPI->{
            NearByAPIChatServiceNavGraph(
                thisDeviceName = thisDeviceUserName,
                onExitRequest = onExitRequest,
                onNewMessageNotificationRequest = onNewMessageNotificationRequest
            )

        }
        Technology.WifiDirect->{
            WifiDirectChatServiceNavGraph(
                thisDeviceUserName = thisDeviceUserName,
                wifiEnabled = wifiEnabled,
                onNewMessageNotificationRequest = onNewMessageNotificationRequest,
                onExitRequest = onExitRequest
            )
        }
        Technology.WifiHotspot-> {
            WifiHotspotChatServiceNavGraph(
                thisDeviceUserName = thisDeviceUserName,
                wifiEnabled = wifiEnabled,
                onNewMessageNotificationRequest = onNewMessageNotificationRequest,
                onExitRequest = {
                    onExitRequest()
                    println("MainNavGraph: onExitRequest")
                },
                onGoBackRequestWithoutStartingChat=onTechSelectRequest
            )
        }
        else-> _NotImplemented()
    }
}

@Composable
private fun _NotImplemented() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Under Construction")
    }

}
