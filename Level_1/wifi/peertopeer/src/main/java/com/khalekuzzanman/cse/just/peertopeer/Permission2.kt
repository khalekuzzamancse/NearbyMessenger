import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionManage(
    permissions: List<String>
) {
    val state = rememberMultiplePermissionsState(permissions)
    val allGranted = state.permissions.all {
        it.status == PermissionStatus.Granted
    }
    LaunchedEffect(Unit) {
        if (!allGranted) {
            state.launchMultiplePermissionRequest()
        }
    }

}