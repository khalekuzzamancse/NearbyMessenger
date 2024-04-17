package kzcse.wifidirect.ui

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun WifiDialog(isWifiEnabled: Boolean, onDismiss: () -> Unit) {
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
