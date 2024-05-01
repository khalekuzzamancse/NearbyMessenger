package nearbyapi.component.common.data_communicator

import nearbyapi.component.common.Message

interface DataCommunicationEvent {

    data class MessageReceived(val msg: Message): DataCommunicationEvent

}