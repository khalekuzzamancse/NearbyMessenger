package navigation

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
internal fun WifiDialog(isWifiEnabled: Boolean) {
    if (!isWifiEnabled) {
        AlertDialog(
            onDismissRequest = { /* Dismiss when user taps outside the dialog */ },
            title = { Text("Wi-Fi is Disabled") },
            text = { Text("Please enable Wi-Fi first.") },
            confirmButton = {

            }
        )
    }
}
