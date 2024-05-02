package peers.ui.devices

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.TabletAndroid
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
internal fun EachDevice(
    device: ScannnedDevice,
    onConnectClick: () -> Unit = {},
    onDeviceInfoClick: () -> Unit = {},
    onDisconnectRequest: () -> Unit = {},
    onClick: () -> Unit = {},
) {
    val isConnected=device.connectionStatus==ConnectionStatus.Connected
    Row(
        modifier = Modifier
            .fillMaxWidth()
//            .clickable {
        //Since the feature is not implemented Yet,that is why disabling the click,so that google play will not found the broken functionality when click it
//                onClick()
//            }
        ,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isConnected) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = "Connected icon",
                tint = MaterialTheme.colorScheme.primary//importance because the whole group is clickable
            )
        } else {
            Icon(
                imageVector = Icons.Default.TabletAndroid,
                contentDescription = "Not connected icon",
                tint = MaterialTheme.colorScheme.primary//importance because the whole group is clickable
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = device.name, style = MaterialTheme.typography.titleMedium)
            Row(modifier = Modifier.fillMaxWidth()) {
                _ConnectionStatus(
                    status =device.connectionStatus,
                    onDisconnectRequest = onDisconnectRequest,
                    onConnectRequest = onConnectClick
                )

            }
        }

        IconButton(
            onClick = onDeviceInfoClick,
            modifier = Modifier
                .semantics { contentDescription = "Device Info" }
                .testTag("DeviceInfoButton")

        ) {
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = "device info",
                tint = MaterialTheme.colorScheme.primary//importance because the whole group is clickable
            )
        }

    }
}

//Making internal so that can access from test module
@Composable
private fun _ConnectionStatus(
    modifier: Modifier = Modifier,
    status: ConnectionStatus,
    onDisconnectRequest: () -> Unit = {},
    onConnectRequest: () -> Unit = {}
) {

    var connectionStatus by remember {
        mutableStateOf(if (status == ConnectionStatus.Connected) "Connected" else "Not Connected")
    }
    val defaultColor=MaterialTheme.colorScheme.error.copy(alpha = 0.5f)
    var connectionStatusColor by remember { mutableStateOf(defaultColor)  }
    val isConnected=status==ConnectionStatus.Connected
    LaunchedEffect(status){
        while (status==ConnectionStatus.Connecting){
            connectionStatusColor=defaultColor.copy(alpha = 1f)
            connectionStatus="Connecting."
            delay(300)
            connectionStatus="Connecting.."
            delay(300)
            connectionStatus="Connecting..."
            delay(300)
        }
        connectionStatus=if (status == ConnectionStatus.Connected) "Connected" else "Not Connected"
    }
    Row(
        modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text=connectionStatus,
            style = MaterialTheme.typography.labelSmall.copy(
                color = connectionStatusColor
            )
        )
        //Connect and disconnect button,if connect prevent user to multiple time click
        if (status!=ConnectionStatus.Connecting){
            Spacer(modifier = Modifier.width(3.dp))
            _ConnectDisconnectButton(modifier, isConnected, onConnectRequest, onDisconnectRequest)
        }



    }

}

@Composable
private fun _ConnectDisconnectButton(
    modifier: Modifier=Modifier,
    isConnected: Boolean,
    onConnectRequest: () -> Unit,
    onDisconnectRequest: () -> Unit,
) {

    Text(
        text = if (isConnected) "Disconnect?" else "Connect?",
        style = MaterialTheme.typography.labelSmall.copy(
            MaterialTheme.colorScheme.primary //Important because Clickable
        ),
        modifier = modifier.clickable {
            if (isConnected) onDisconnectRequest()
            else onConnectRequest()
        }
    )

}
