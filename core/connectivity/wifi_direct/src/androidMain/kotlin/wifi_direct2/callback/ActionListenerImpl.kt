package wifi_direct2.callback

import android.net.wifi.p2p.WifiP2pManager

/**
 * @param _onSuccess Code for when the discovery/connection initiation is successful goes here.No services have actually
 * been discovered yet, so this method can often be left blank. Code for peer discovery goes in th onReceive method,
 * detailed below.
 * @param _onFailure  Code for when the discovery initiation fails goes here.  Alert the user that something went wrong
 */
class ActionListenerImpl(
    private val _onSuccess:()->Unit,
    private val _onFailure:(Throwable)->Unit,
): WifiP2pManager.ActionListener {

    override fun onSuccess()=_onSuccess()

    override fun onFailure(code: Int) {
       _onFailure(code._Throwable())
    }
    @Suppress("FunctionName")
    private fun Int._Throwable()=when(this){
            WifiP2pManager.P2P_UNSUPPORTED -> Throwable("Your device does not support P2P")
            WifiP2pManager.BUSY -> Throwable(" framework is busy and unable to service the request")
            WifiP2pManager.ERROR -> Throwable("failed due to an internal error")
            else -> Throwable("Unknown error")
        }

}
