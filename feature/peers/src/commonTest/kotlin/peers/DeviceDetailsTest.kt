package peers

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import peers.devices_list.DeviceDetails
import peers.devices_list.NearByDevice
import kotlin.test.Test

class DeviceDetailsTest {
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun connectedDeviceInfoTest() = runComposeUiTest {

        val connectDevice = NearByDevice(name = "Md Abdul", isConnected = true, ip = "1234")
        setContent {
            DeviceDetails(
                device = connectDevice,
                onClose = {},
                onDisconnectRequest = {}
            )
        }
        onNodeWithTag(":DeviceNameText").assertTextEquals("Device Name: Md Abdul")
        onNodeWithTag(":IPAddressText").assertTextEquals("IP Address: 1234")

        //on connected "Cancel" Button will be will be ,"Disconnect" will be shown
        onNodeWithTag(":Dialog:DisconnectButton").assertExists()
        onNodeWithTag(":Dialog:CancelButton").assertDoesNotExist()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun disConnectedDeviceInfoTest() = runComposeUiTest {
        val functionName = "DeviceDetails"
        val device = NearByDevice(name = "Md Abdul", isConnected = false, ip = "1234")
        setContent {
            DeviceDetails(
                device = device,
                onClose = {},
                onDisconnectRequest = {}
            )
        }
        onNodeWithTag(":$functionName:DeviceNameText").assertTextEquals("Device Name: Md Abdul")
        onNodeWithTag(":$functionName:IPAddressText").assertTextEquals("IP Address: 1234")
        //on connected "Cancel" Button will be will be ,"Disconnect" will be shown
        onNodeWithTag(":$functionName:Dialog:DisconnectButton").assertDoesNotExist()
        onNodeWithTag(":$functionName:Dialog:CancelButton").assertExists()
    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun dismissDialog() = runComposeUiTest {
        val functionName = "DeviceDetails"
        val device = NearByDevice(name = "Md Abdul", isConnected = false, ip = "1234")
        var showDialog by mutableStateOf(true)

        setContent {
            if (showDialog) {
                DeviceDetails(
                    modifier = Modifier.testTag(":$functionName:Dialog"),
                    device = device,
                    onClose = {
                        showDialog = false
                    },
                    onDisconnectRequest = {
                        showDialog = false
                    }
                )
            }

        }
        onNodeWithTag(":$functionName:Dialog").assertExists()
        //Click for dismiss dialog
        onNodeWithTag(":$functionName:Dialog:CancelButton").performClick()
        onNodeWithTag(":$functionName:Dialog").assertDoesNotExist()
    }
}
