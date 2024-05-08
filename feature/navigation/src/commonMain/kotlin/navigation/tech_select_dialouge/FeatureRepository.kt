package navigation.tech_select_dialouge

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiTethering
import androidx.compose.ui.text.AnnotatedString

internal object FeatureRepository {

    fun getFeatures(): List<Feature> {
        return listOf(
            createNearByAPIFeature(),
            createWiFiDirectFeature(),
            createWiFiHotspotFeature()
        )
    }

    private fun createNearByAPIFeature(): Feature {
        return Feature(
            name = "Chat with Near-By APIs",
            icon = Icons.Filled.Devices,
            usedTechNames = "Uses Combination of Wi-Fi and Bluetooth technologies",
            details = createNearByAPIsDetails()
        )
    }

    private fun createWiFiDirectFeature(): Feature {
        return Feature(
            name = "Chat with Wi-Fi Direct",
            icon = Icons.Filled.Wifi,
            usedTechNames = "Uses WiFi P2P",
            details = createWiFiDirectDetails()
        )
    }

    private fun createWiFiHotspotFeature(): Feature {
        return Feature(
            name = "Chat with Wi-Fi Hotspot",
            icon = Icons.Filled.WifiTethering,
            usedTechNames = "Uses Wi-Fi tethering",
            details = createHotSpotDetails()
        )
    }

    private fun createNearByAPIsDetails(): Details {
        return Details(
            techUsed = listOf(
                AnnotatedString("Communicates with nearby devices regardless of the underlying technology")
            ),
            userManual = listOf(
                AnnotatedString("Ensure your device's Bluetooth is turned on"),
                AnnotatedString("Ensure your device's Wi-Fi is turned on"),
                AnnotatedString("Turn off Wi-Fi hotspot if it is active"),
                AnnotatedString("Among all devices, one will act as the Advertiser and the remaining will be Discoverers")

            )
        )
    }

    private fun createWiFiDirectDetails(): Details {
        return Details(
            techUsed = listOf(
                AnnotatedString("Enables communication without a network connection such as a router or hotspot"),
                AnnotatedString("Offers faster data transmission rates than Wi-Fi hotspot"),
                AnnotatedString("Not all devices support this feature")
            ),
            userManual = listOf(
                AnnotatedString("Start scanning for devices"),
                AnnotatedString("If the initial scan does not find any devices, continue scanning until devices are detected"),
                AnnotatedString("Once your device becomes visible to your friend's device and vice versa, initiate the connection process"),
                AnnotatedString("If you encounter any unexpected issues or cannot establish a connection, restart the application and try again"),
                AnnotatedString("Supports connections with multiple devices simultaneously")
            )
        )
    }

    private fun createHotSpotDetails(): Details {
        return Details(
            techUsed = listOf(
                AnnotatedString("Data transmission rate is slower compared to Wi-Fi Direct")
            ),
            userManual = listOf(
                AnnotatedString("If you are not the hotspot owner, ensure you are connected to the hotspot"),
                AnnotatedString("If unexpected issues arise or a connection cannot be established, restart the application and try again"),
                AnnotatedString("Allows connection with multiple devices simultaneously")
            )
        )
    }


}
