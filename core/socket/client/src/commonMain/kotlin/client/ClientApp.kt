package client

import client.data_communication.TextMessage

/**
 * Must run the server and the client as separate process.
 * not within the same process.otherwise they will not able to work
 * properly
 */
fun main() {

    println("Enter the SERVER port")
    val port = readln().toInt()
    println("Enter username")
    val userName = readln()
    val app = Client(userName, port)
    app.listenForMessage()
    while (true) {
        println("Enter sender ip:")
        val senderIP= readln()
        println("Enter sender port:")
        val senderPort= readln().toInt()
        println("Enter Message Body :")
        val msgBody: String = readln()
        val msg= TextMessage(
            senderIp = senderIP,
            senderPort = senderPort,
            timestamp = System.currentTimeMillis(),
            message = msgBody
        )
        app.sendMessage(msg)
    }

}