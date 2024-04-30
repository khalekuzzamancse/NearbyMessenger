package nearbyapi

import android.content.Context
import nearbyapi.component.advertiser.AdvertiserImpl
import nearbyapi.component.discoverer.DiscovererImpl

object Factory {
    fun createAdvertiser(context:Context, name:String, )= AdvertiserImpl(context, name)
    fun createDiscover(context: Context,name: String)= DiscovererImpl(context,name)
}