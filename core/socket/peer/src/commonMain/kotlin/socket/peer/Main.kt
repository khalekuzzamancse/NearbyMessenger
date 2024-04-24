package socket.peer

import core.socket.client.Client

fun main() {
    println("Main")

    val input=readln()
    if (input=="YES"){
        try {
            val client = Client("A", "localhost", 8080)
        }
        catch (e:Exception){
            println(e.message)
        }
    }


}