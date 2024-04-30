package nsd.common.data_communicator

import nsd.common.Message

interface DataCommunicationEvent {

    data class MessageReceived(val msg:Message):DataCommunicationEvent

}