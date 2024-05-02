package peers.ui.devices

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp


//Making internal so that can access from test module
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DeviceDetails(
    modifier: Modifier = Modifier,
    device: ScannnedDevice,
    onClose: () -> Unit
) {

    AlertDialog(
        onDismissRequest = onClose,
        modifier = modifier
            .semantics { contentDescription = "Device info" }

    ) {
        Surface(
            modifier = Modifier,
            shape = RoundedCornerShape(32.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Text(
                    text = "Device Name: ${device.name}",
                    modifier = Modifier
                        .testTag(":DeviceNameText")
                )
                Text(
                    text = "IP Address: ${device.id}",
                    modifier = Modifier
                        .testTag(":IPAddressText")
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                        TextButton(
                            onClick = onClose,
                            modifier = Modifier
                                .semantics { contentDescription = "Dismiss Details Dialogue" }
                                .testTag(":Dialog:CancelButton")
                        ) {
                            Text(text = "Cancel")
                        }
                    }

                }


        }


    }


}
