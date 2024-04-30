package nsd.common.conformer

internal data class AuthToken(
    val endpointId: String,
    val name:String,
    val pin:String,
)