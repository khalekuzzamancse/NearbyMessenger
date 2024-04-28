package peers.ui.route

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonSearch
import androidx.compose.material.icons.filled.WifiFind
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
import androidx.compose.ui.unit.sp

@Composable
internal fun WifiDirectDevicesHeader(
    modifier: Modifier,
    onScanDeviceRequest: () -> Unit,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        _Title(modifier = Modifier)
        _ScanButton(modifier = Modifier, onClick = onScanDeviceRequest)
    }

}

@Composable
private fun _Title(
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier) {
        Icon(
            imageVector = Icons.Default.WifiFind,
            contentDescription = "Wifi direct device icon",
            tint = MaterialTheme.colorScheme.tertiary// because not  clickable
        )
        Spacer(Modifier.width(4.dp))
        Text(
            text = "Wifi-Direct Devices",
            fontSize = 18.sp
        //Not clickable ,so do not need to bold
        // ,not importance as conversation,just works like bill board on a shop
        )
    }
}

@Composable
private fun _ScanButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .semantics { contentDescription = "Scan Device " }
            .testTag("ScanDeviceButton"),
    ) {
        Icon(
            imageVector =    Icons.Default.PersonSearch,
            contentDescription = "Scan device icon",
            tint = MaterialTheme.colorScheme.primary//Important because clickable
        )
    }

}