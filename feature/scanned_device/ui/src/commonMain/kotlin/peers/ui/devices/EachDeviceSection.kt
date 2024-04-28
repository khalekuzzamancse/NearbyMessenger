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
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

@Composable
internal fun EachDevice(
    device: NearByDevice,
    onConnectClick: () -> Unit = {},
    onDeviceInfoClick: () -> Unit = {},
    onDisconnectRequest: () -> Unit = {},
    onClick: () -> Unit = {},
) {
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
        if (device.isConnected) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = "Connected icon",
                tint = MaterialTheme.colorScheme.primary//importance because the whole group is clickable
            )
        } else {
            Icon(
                imageVector = Icons.Default.WifiOff,
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
                    isConnected = device.isConnected,
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
    isConnected: Boolean,
    onDisconnectRequest: () -> Unit = {},
    onConnectRequest: () -> Unit = {}
) {
    Row(
        modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = if (isConnected) "Connected" else "Not Connected",
            style = MaterialTheme.typography.labelSmall.copy(
                color = MaterialTheme.typography.labelSmall.color.copy(alpha = 0.5f)
            )
        )
        Spacer(modifier = Modifier.width(3.dp))
        Text(
            text = if (isConnected) "Disconnect?" else "Connect?",
            style = MaterialTheme.typography.labelSmall.copy(
               MaterialTheme.colorScheme.primary //Important because Clickable
            ),
            modifier = Modifier.clickable {
                if (isConnected) onDisconnectRequest()
                else onConnectRequest()
            }
        )

    }

}
