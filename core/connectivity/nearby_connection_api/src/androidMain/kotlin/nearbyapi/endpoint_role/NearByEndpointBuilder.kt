package nearbyapi.endpoint_role

import android.content.Context

class NearByEndpointBuilder(
    private val context: Context,
    private val name: String
) {

    fun build(type: EndPointType): NearByEndPoint {
        return when (type) {
            EndPointType.Advertiser -> AdvertiserEndPoint(context, name)
            EndPointType.Discoverer -> DiscovererEndPoint(context, name)
        }

    }

}