package blueetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.util.Log
import java.io.IOException
import java.util.UUID

/**
 * - Note that when accept() returns the BluetoothSocket,
 * the socket is already connected. Therefore, you shouldn't call connect(), as you do from the client side.
 */
@SuppressLint("MissingPermission")
internal class AcceptThread(
    bluetoothAdapter: BluetoothAdapter?
) : Thread() {

    private val mmServerSocket: BluetoothServerSocket? by lazy(LazyThreadSafetyMode.NONE) {
        val uuid = UUID.fromString("905c2a8f-f6ed-48da-8682-d6ce38fc91ff")
        bluetoothAdapter?.listenUsingInsecureRfcommWithServiceRecord("NAME", uuid)
    }

    override fun run() {
        // Keep listening until exception occurs or a socket is returned.
        var shouldLoop = true
        while (shouldLoop) {
            val socket: BluetoothSocket? = try {
                mmServerSocket?.accept()
            } catch (e: IOException) {
                log("Socket's accept() method failed :$e")
                shouldLoop = false
                null
            }
            socket?.also {
                //  manageMyConnectedSocket(it)
                mmServerSocket?.close()
                shouldLoop = false
            }
        }
    }

    // Closes the connect socket and causes the thread to finish.
    fun cancel() {
        try {
            mmServerSocket?.close()
        } catch (e: IOException) {
            log("Could not close the connect socket:$e")
        }
    }

    @Suppress("Unused")
    private fun log(message: String, methodName: String? = null) {
        val tag = "${this@AcceptThread::class.simpleName}Log:"
        val method = if (methodName == null) "" else "$methodName()'s "
        val msg = "$method:-> $message"
        Log.d(tag, msg)
    }
}