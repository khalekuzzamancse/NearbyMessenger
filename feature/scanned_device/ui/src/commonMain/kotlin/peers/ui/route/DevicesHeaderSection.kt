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
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DataUsage
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * @param title is for the header title such as for Wifi direct can be used "Wifi Direct Deices",
 * for Bluetooth can be used "Bluetooth Devices"
 * @param icon the logo for the header,for wifi devices use Wifi related icon,for Bluetooth devices use
 * bluetooth related icon
 */
@Composable
 fun DevicesHeaderSection(
    modifier: Modifier,
    title:String,
    icon:ImageVector,
    isScanning: Boolean,
    onScanDeviceRequest: () -> Unit,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        _Title(modifier = Modifier,icon,title)
        _RotatingScanIcon(Modifier, isScanning = isScanning, onClick = onScanDeviceRequest)
    }

}

@Composable
private fun _Title(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String,
) {
    Row(modifier = modifier) {
        Icon(
            imageVector = icon,
            contentDescription = " device icon",
            tint = MaterialTheme.colorScheme.tertiary// because not  clickable
        )
        Spacer(Modifier.width(4.dp))
        Text(
            text = title,
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


