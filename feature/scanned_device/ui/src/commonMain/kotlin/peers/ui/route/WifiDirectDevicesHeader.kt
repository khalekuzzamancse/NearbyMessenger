package peers.ui.route

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DataUsage
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.WifiFind
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
internal fun WifiDirectDevicesHeader(
    modifier: Modifier,
    isScanning: Boolean,
    onScanDeviceRequest: () -> Unit,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        _Title(modifier = Modifier)
        _RotatingScanIcon(Modifier, isScanning = isScanning, onClick = onScanDeviceRequest)
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
private fun _RotatingScanIcon(
    modifier: Modifier = Modifier,
    isScanning: Boolean,
    onClick: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = ""
    )
    IconButton(
        modifier = modifier
            .testTag("ScanDeviceButton"),
        onClick = onClick
    ) {
        Icon(
            imageVector = Icons.Filled.DataUsage, // You can replace this with any appropriate icon
            //Equivalent icon are:Auto renew,donut large,loop,sync
            contentDescription = "Scan Device",
            tint = MaterialTheme.colorScheme.primary,//Important because clickable
            modifier = Modifier
                .graphicsLayer {
                    rotationZ = if (isScanning) angle else 0f
                }

        )
    }

}


