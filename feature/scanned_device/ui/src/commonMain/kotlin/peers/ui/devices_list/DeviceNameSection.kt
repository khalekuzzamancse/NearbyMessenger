package peers.ui.devices_list
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TabletMac
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DeviceNameSection(
    modifier: Modifier=Modifier,
    thisDeviceName: String) {
    Row(
        modifier=modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Default.TabletMac, "UserName icon"
        )
        Spacer(Modifier.width(4.dp))
        Text(thisDeviceName)
    }

}
