package wifi_hotspot_chat_service.join_as_dialogue

import android.content.Intent
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.launch
import wifi_hotspot_chat_service.misc.SnackBarDecorator

@Composable
internal fun ConfirmHotspotOwnerDialog(
    onNavigationRequest: () -> Unit,
) {

    val controller = remember { HotspotDialogController() }
    val scope = rememberCoroutineScope()
    val startActivityIntent = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        controller.onSettingClosed()

    }
    LaunchedEffect(Unit) {
        controller.shouldLaunchSettings.collect { launchSetting ->
            if (launchSetting) {
                startActivityIntent.launch(Intent(Settings.ACTION_WIFI_SETTINGS))
            }
        }
    }
    val hostState = remember { SnackbarHostState() }

    val errorMessage = controller.errorMessage.collectAsState().value
    LaunchedEffect(errorMessage) {
        if (errorMessage != null)
            hostState.showSnackbar(message = errorMessage, duration = SnackbarDuration.Long)
    }


    AlertDialog(
        modifier = Modifier,
        onDismissRequest = { },
        title = { Text("Confirm ") },
        text = {
            Box(
            ) {
                Text(_getHotspotDialogDescription())
                //Show snack bar on top of the dialog so that the message is visible to user
                SnackbarHost(hostState = hostState, Modifier.align(Alignment.BottomCenter))
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    scope.launch {
                        val shouldNavigate = controller.verifyHotspotOwner()
                        if (shouldNavigate)
                            onNavigationRequest()
                    }

                }
            ) {
                Text("Yes")
            }
        },
        dismissButton = {
            Button(
                onClick = {
                    scope.launch {
                        val shouldNavigate = controller.verifyHotspotConsumer()
                        if (shouldNavigate)
                            onNavigationRequest()
                    }
                }
            ) {
                Text("No")
            }
        },
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
    )


}

@Composable
private fun _getHotspotDialogDescription(): AnnotatedString {
    return buildAnnotatedString {
        append("Due to security restrictions by the Android OS, this app cannot automatically ")

        withStyle(
            style = SpanStyle(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        ) {
            append("enable")
        }
        append(" or ")
        withStyle(
            style = SpanStyle(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        ) {
            append("connect")
        }
        append(" to a Wi-Fi hotspot. If you are the owner of the hotspot, please ")
        withStyle(
            style = SpanStyle(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        ) {
            append("Turn on")
        }
        append(" the hotspot manually and then press ")
        withStyle(
            style = SpanStyle(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.error
            )
        ) {
            append("\"Yes\"")
        }
        append(". If you are not the owner of the hotspot and wish to ")
        withStyle(
            style = SpanStyle(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        ) {
            append("connect")
        }
        append(" to an already active hotspot, please press ")
        withStyle(
            style = SpanStyle(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.error
            )
        ) {
            append("\"No\"")
        }
        append(".")
    }
}