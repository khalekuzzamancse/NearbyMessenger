package nsd

import android.content.Context
import nsd.advertiser.AdvertiserImpl
import nsd.discoverer.DiscovererImpl

object Factory {
    fun createAdvertiser(context:Context, name:String, )=AdvertiserImpl(context, name)
    fun createDiscover(context: Context,name: String)=DiscovererImpl(context,name)
}