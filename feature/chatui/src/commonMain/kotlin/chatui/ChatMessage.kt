package chatui
data class ChatMessage(
    val message: String,
    val timestamp: String,
    val isSender: Boolean,
)

fun getDummyConversation():List<ChatMessage>{
    val dummyConversation = mutableListOf<ChatMessage>()
    // Adding some sample messages
    dummyConversation.add(ChatMessage("Hello there!", "2023-11-03 10:00 AM", true))
    dummyConversation.add(
        ChatMessage(
            "Hi! How can I help you?",
            "2023-11-03 10:05 AM",
            false
        )
    )
    dummyConversation.add(
        ChatMessage(
            "I have a question about your product.",
            "2023-11-03 10:10 AM",
            true
        )
    )
    dummyConversation.add(
        ChatMessage(
            "Sure, feel free to ask.",
            "2023-11-03 10:15 AM",
            false
        )
    )
    dummyConversation.add(
        ChatMessage(
            "What are the features of your latest product?",
            "2023-11-03 10:20 AM",
            true
        )
    )
    return dummyConversation
}