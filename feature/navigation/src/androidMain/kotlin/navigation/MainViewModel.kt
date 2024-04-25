package navigation;

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import chatui.viewmodel.ChatViewModel
import peers.ui.scanneddevice.DeviceListViewModel
import peers.ui.scanneddevice.DevicesConnectionInfo

class MainViewModel : ViewModel() {
    private val dataCommunicator = DataCommunicatorImpl()
    val chatViewModel =ChatViewModel(dataCommunicator)
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    val deviceListViewModel= DeviceListViewModel()
     fun onGroupFormed(info: DevicesConnectionInfo)=dataCommunicator.onGroupFormed(info)

}
