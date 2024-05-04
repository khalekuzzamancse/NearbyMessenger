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
                AnnotatedString("Communicate with NearBy devices regardless of underlying technologies")
            ),
            userManual = listOf(
                AnnotatedString("Ensure your device's Bluetooth is turned on"),
                AnnotatedString("Ensure your device's Wi-Fi is turned on"),
                AnnotatedString("Turn off the  Wi-Fi hotspot if active")
            )
        )

    }


    private fun createWiFiDirectDetails(): Details {
        return Details(
            techUsed = listOf(
                AnnotatedString("Can communicate without being connected to any network such as a Router or Hotspot"),
                AnnotatedString("Data transmission rate is faster than Wi-Fi Hotspot"),
                AnnotatedString("Not all devices have built-in support for it")
            ),
            userManual = listOf(
                AnnotatedString("Start scanning for devices"),
                AnnotatedString("If a one-time scan does not find any devices, continue scanning until devices are found"),
                AnnotatedString("When your device appears to your friend's device and vice versa, start the connection process"),
                AnnotatedString("If unexpected bugs occur or connection cannot be established, relaunch the application and try again"),
                AnnotatedString("Multiple devices can be connected simultaneously")
            )
        )
    }

    private fun createHotSpotDetails(): Details {
        return Details(
            techUsed = listOf(
                AnnotatedString("Data transmission rate is slower than Wi-Fi Direct")
            ),
            userManual = listOf(
                AnnotatedString("If you are not the Hotspot owner, ensure you are connected to the Hotspot"),
                AnnotatedString("If unexpected bugs occur or connection cannot be established, relaunch the application and try again"),
                AnnotatedString("Multiple devices can be connected simultaneously")
            )
        )

}

}
