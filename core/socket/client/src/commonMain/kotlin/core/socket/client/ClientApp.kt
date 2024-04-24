package core.socket.client

/**
 * Must run the server and the client as separate process.
 * not within the same process.otherwise they will not able to work
 * properly.
 * *  to test and run as separate program run the following in Terminal or CMD:
 * ```
 * -  .\core\socket\client\src\commonMain\kotlin\socket\client
 * - kotlinc ClientApp.kt -include-runtime -d ClientApp.jar
 * - java -jar ClientApp.jar
 * ```
 *
 */
fun main() {
    println("Enter the SERVER port")
    val port = readln().toInt()
    println("Enter username")
    val userName = readln()
    val app = Client(userName, serverIp = "localhost", serverPort = port)
//    app.listenForMessage()
//    while (true) {
//        println("Enter sender ip:")
//        val senderIP= readln()
//        println("Enter sender port:")
//        val senderPort= readln().toInt()
//        println("Enter Message Body :")
//        val msgBody: String = readln()
//        val msg= TextMessage(
//            senderIp = senderIP,
//            senderPort = senderPort,
//            timestamp = System.currentTimeMillis(),
//            message = msgBody
//        )
//        app.sendMessage(msg)
//    }

}