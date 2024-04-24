package peers

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import peers.ui.devices_list.NearByDevice
import peers.ui.devices_list.NearByDevicesRoute
import kotlin.test.Test

class DeviceListRouteListTestAndroid {
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun scanButtonTesting() = runComposeUiTest {
        var showProgressBar by mutableStateOf(false)
        setContent {
            NearByDevicesRoute(
                devices = emptyList(),
                wifiEnabled = true,
                showProgressbar = showProgressBar,
                onDisconnectRequest = {},
                onConnectionRequest = {},
                onConversionScreenOpenRequest = {},
                onScanDeviceRequest = {
                    showProgressBar = true
                },
                onWifiStatusChangeRequest = {

                }
            )
        }
        //Testing
        //initially device list is present,with empty list
        onNodeWithTag("OnLoadingContent").assertDoesNotExist()
        onNodeWithTag("NearByDevicesList").assertExists()
        //onScanning Device list is hidden
        onNodeWithTag("ScanDeviceButton").performClick()
        onNodeWithTag("OnLoadingContent").assertExists()
        onNodeWithTag("NearByDevicesList").assertDoesNotExist()

    }

    @OptIn(ExperimentalTestApi::class)
    @Test
    fun wifiOnOffButtonTest() = runComposeUiTest {
        var wifiEnabled by mutableStateOf(false)
        setContent {
            NearByDevicesRoute(
                devices = emptyList(),
                wifiEnabled = wifiEnabled,
                showProgressbar = false,
                onDisconnectRequest = {},
                onConnectionRequest = {},
                onConversionScreenOpenRequest = {},
                onScanDeviceRequest = {},
                onWifiStatusChangeRequest = {
                    wifiEnabled = !wifiEnabled
                }
            )
        }
        //Testing
        //initially wifi is not enabled
        onNodeWithTag("WifiOffIcon").assertDoesNotExist()
        //
        onNodeWithTag("WifiStatusChangeButton").performClick()
        onNodeWithTag("WifiOnIcon").assertDoesNotExist()
    }
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun deviceInfoButtonCheck() = runComposeUiTest {
        val devices = listOf(
            NearByDevice(name = "Md Abdul", isConnected = true, ip = "1234"),
        )
        setContent {
            NearByDevicesRoute(
                devices = devices,
                wifiEnabled = false,
                showProgressbar = false,
                onDisconnectRequest = {},
                onConnectionRequest = {},
                onConversionScreenOpenRequest = {},
                onScanDeviceRequest = {},
                onWifiStatusChangeRequest = {

                }
            )
        }
        //Testing
        onNodeWithTag("DeviceInfoButton").performClick()
        onNodeWithTag("DeviceDetailsDialogue").assertExists()
    }


}
