package kzcse.wifidirect

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import navigation.navgraph.Technology
import wifidirect.WifiDirectFactory
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
class AppViewModel:ViewModel() {
    /**For first time installation or after clean app data ,user need to provide a device name*/
     var showUserNameDialog by mutableStateOf(false)
        private set


    /** Wifi direct,Hotspot and NearbyAPI causes problem if they active together
     * that is why giving single time choice to choose a technology*/
     var selectedTech by mutableStateOf<Technology?>(null)
        private set

    val wifiEnabled= WifiDirectFactory.broadcastNConnectionHandler.isWifiEnabled
    fun showNameInputDialoge(){
        showUserNameDialog=true
    }
    fun  onTechSelected(technology: Technology){
        selectedTech=technology
    }
    fun onNameInputCompleted(){
        showUserNameDialog=false
    }
}