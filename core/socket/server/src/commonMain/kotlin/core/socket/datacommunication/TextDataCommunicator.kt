package core.socket.datacommunication

/**
 * Interface for communicating text data over a TCP/IP socket connection.
 * Allows sending and receiving text messages up to a size of 4KB.
 *
 * Approximations based on UTF-8 encoding:
 * - Characters: Up to approximately 4096 characters if only ASCII characters are used.
 * - Words: Assuming an average word length of 5 characters plus a space, approximately 680 words.
 * - Sentences: Assuming an average sentence length of 15-20 words, approximately 34-45 sentences.
 */
interface TextDataCommunicator {
    /**
     * Sends a text message using the specified socket.
     * The message should not exceed 4KB in size.
     *
     * @param message The text message to be sent.
     * @return A Result<Unit> indicating success or failure.
     */
    suspend fun sendMessage(message: String): Result<Unit>

    /**
     * Retrieves a string of data received through the specified socket.
     * This method will read data up to 4KB from the socket's input stream.
     *
     * @return The received text data as a String, or null if no data was received or an error occurred.
     */
    suspend fun retrieveReceivedData():List<String>
}
