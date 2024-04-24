package navigation;

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import chatui.viewmodel.ChatViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import peers.ui.scanneddevice.DeviceListViewModel
import peers.ui.scanneddevice.DevicesConnectionInfo
import socket.peer.Communicator

class MainViewModel : ViewModel() {
    private val dataCommunicator = DataCommunicatorImpl()
    val chatViewModel =ChatViewModel(dataCommunicator)
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    val deviceListViewModel= DeviceListViewModel()
     fun onGroupFormed(info: DevicesConnectionInfo)=dataCommunicator.onGroupFormed(info)
}
