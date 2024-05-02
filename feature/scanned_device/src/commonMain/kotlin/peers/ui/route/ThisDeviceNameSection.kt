package peers.ui.route
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TabletMac
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ThisDeviceNameSection(
    modifier: Modifier=Modifier,
    thisDeviceName: String) {
    Row(
        modifier=modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.TabletMac,
            contentDescription = "UserName icon",
        )
        Spacer(Modifier.width(4.dp))
        Text(
            text = thisDeviceName,
            style = MaterialTheme.typography.titleMedium.copy(
                color = MaterialTheme.colorScheme.tertiary// importance for other device to connect,but not clickable
            )
        )
    }

}
