package chatui.message

/**
 * @param senderName ,if device is sender then null
 */
data class ChatMessage(
    val senderName: String?,
    val message: String,
    val timestamp: String,
)


internal fun dummyConversation(): List<ChatMessage> {
    val dummyConversation = mutableListOf<ChatMessage>()
    // Adding some sample messages
    dummyConversation.add(
        ChatMessage(
            senderName = null,
            message = "Hello there!",
            timestamp = "2023-11-03 10:00 AM"
        )
    )
    dummyConversation.add(
        ChatMessage(
            null,
            "Hi! How can I help you?",
            "2023-11-03 10:05 AM",
        )
    )
    dummyConversation.add(
        ChatMessage(
            "Md Abdul",
            "I have a question about your product.",
            "2023-11-03 10:10 AM",

            )
    )
    dummyConversation.add(
        ChatMessage(
            "Md Abdul",
            "Sure, feel free to ask.",
            "2023-11-03 10:15 AM",

            )
    )
    dummyConversation.add(
        ChatMessage(
            null,
            "What are the features of your latest product?",
            "2023-11-03 10:20 AM",
        )
    )
    return dummyConversation
}