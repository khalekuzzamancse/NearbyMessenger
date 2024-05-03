package wifi_hotspot_chat_service.join_as_dialogue

import core.wifi_hotspot.WiFiHotspotFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class HotspotDialogController {
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()
    private val _shouldLaunchSettings = MutableStateFlow(false)
    val shouldLaunchSettings = _shouldLaunchSettings.asStateFlow()
    private var firstTimeRequestForConsumerShip = true
    private val isWifiEnabled
        get() = WiFiHotspotFactory.receiver.isWifiEnabled()

    fun isConnectedWithWifi() = WiFiHotspotFactory.receiver.isWifiConnected()
    private fun launchSettingApp() =_shouldLaunchSettings.update { true }

    fun onSettingClosed() =_shouldLaunchSettings.update { false }


    /**
     * - Return true if the the device wifi is not enabled
     */
   suspend fun verifyHotspotOwner():Boolean {
        if (isWifiEnabled) {
            updateErrorMessage("Your Wi-Fi is turned ON, please turn it OFF and start a Hotspot from Setting app")
            delay(2000)
            launchSettingApp()
            return false
        }
        return true
    }

    /**
     * return true if the device is connected with the hotspot/any network(wifi)
     */
    suspend fun verifyHotspotConsumer():Boolean {
        if (firstTimeRequestForConsumerShip) {
            onFirstTimeRequestForConsumerShip()
            return false
        } else {
            if (!isConnectedWithWifi()) {
                updateErrorMessage("Please Connect with the Hotspot")
                delay(2000)
                launchSettingApp()
                return false
            }

        }
        return true
    }

    private suspend fun onFirstTimeRequestForConsumerShip() {
        updateErrorMessage("Please select the hotspot to connect")
        delay(2000)
        launchSettingApp()
        firstTimeRequestForConsumerShip = false

    }


    private fun updateErrorMessage(message: String) {
        CoroutineScope(Dispatchers.Default).launch {
            _errorMessage.update { message }
            delay(3000)
            _errorMessage.update { null }
        }
    }

}
