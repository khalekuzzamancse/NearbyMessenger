package nearbyapi.component.common.endpoint


/**
 * - The devices we've discovered near us :Status=[EndPointStatus.Discovered]
 * - The devices we have pending connections to,Status=[EndPointStatus.Initiated] until we ,call [ ][.acceptConnection] or [.rejectConnection]
 * - Status=[EndPointStatus.Connected] The devices we are currently connected to. For advertisers, this may be more than one. For discoverers, there will
 * only be one entry in this set.
 */
data class EndPointInfo(
    val id:String,
    val name:String,
    val status: EndPointStatus
)
