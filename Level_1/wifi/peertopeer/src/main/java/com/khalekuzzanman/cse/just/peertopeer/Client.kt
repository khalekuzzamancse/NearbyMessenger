package com.khalekuzzanman.cse.just.peertopeer

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket

class Client(
    hostAddress:InetAddress
){
    private val clientSocket=Socket()
    init {
        val scope = CoroutineScope(Dispatchers.Default)
        scope.launch {

            withContext(Dispatchers.IO) {
                try {
                  clientSocket.connect(InetSocketAddress(hostAddress.hostAddress,8888),2000)
                }
                catch (e:Exception){
                    e.printStackTrace()
                }

            }
        }
    }
}