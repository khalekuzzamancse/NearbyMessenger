package com.khalekuzzanman.cse.just.peertopeer

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.ServerSocket
import java.net.Socket

class Server {
    private val serverSocket = ServerSocket(8888)
    private var connectedClientSocket: Socket? = null

    init {
        val scope = CoroutineScope(Dispatchers.Default)
        scope.launch {

            withContext(Dispatchers.IO) {
                try {
                    connectedClientSocket = serverSocket.accept()
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }
    }

}