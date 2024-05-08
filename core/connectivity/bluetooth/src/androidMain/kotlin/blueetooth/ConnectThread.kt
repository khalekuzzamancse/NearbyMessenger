package blueetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.util.Log
import java.io.IOException
import java.util.UUID

@SuppressLint("MissingPermission")
internal class ConnectThread(
    private val bluetoothAdapter: BluetoothAdapter?,
    device: BluetoothDevice
) : Thread() {
    private val uuid = UUID.fromString("905c2a8f-f6ed-48da-8682-d6ce38fc91ff")
   private val mmSocket: BluetoothSocket? by lazy(LazyThreadSafetyMode.NONE) {
       device.createRfcommSocketToServiceRecord(uuid)
   }

   public override fun run() {
       // Cancel discovery because it otherwise slows down the connection.
       bluetoothAdapter?.cancelDiscovery()

       mmSocket?.let { socket ->
           // Connect to the remote device through the socket. This call blocks
           // until it succeeds or throws an exception.
           socket.connect()

           // The connection attempt succeeded. Perform work associated with
           // the connection in a separate thread.
     //      manageMyConnectedSocket(socket)
       }
   }

   // Closes the client socket and causes the thread to finish.
   fun cancel() {
       try {
           mmSocket?.close()
       } catch (e: IOException) {
           log("Could not close the client socket, $e")
       }
   }
    @Suppress("Unused")
    private fun log(message: String, methodName: String? = null) {
        val tag = "${this@ConnectThread::class.simpleName}Log:"
        val method = if (methodName == null) "" else "$methodName()'s "
        val msg = "$method:-> $message"
        Log.d(tag, msg)
    }
}