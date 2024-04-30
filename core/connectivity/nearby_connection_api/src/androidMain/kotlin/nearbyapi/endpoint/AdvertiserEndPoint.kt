package nearbyapi.endpoint

import android.content.Context
import kotlinx.coroutines.flow.StateFlow
import nearbyapi.Factory
import nearbyapi.component.common.Message
import nearbyapi.component.common.endpoint.EndPointInfo

/**
 * - Prevent client code to create instance of it to avoid tight couping
 */
class AdvertiserEndPoint internal constructor(
    context: Context,
    name: String,
) :NearByEndPoint {
    private val advertiser= Factory.createAdvertiser(context, name)
    override val endPoints: StateFlow<Set<EndPointInfo>> =advertiser.discoverers
    override val receivedMessage: StateFlow<Message?> =advertiser.receivedMessage
    override suspend fun scan()=advertiser.startAdvertising()


    override suspend fun initiateConnection(endpointId:String): Result<Unit> {
        return Result.failure(Throwable("Not implement yet"))
    }
    override suspend fun sendMessage(msg: Message)=advertiser.sendMessage(msg)
}