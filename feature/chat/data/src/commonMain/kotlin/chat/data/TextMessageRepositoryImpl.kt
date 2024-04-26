package chat.data

import chat.domain.model.TextMessageModel
import chat.domain.model.TextMessageModelRole
import chat.domain.reposiory.TextMessageRepository
import core.database.api.TextMessageAPIs
import core.database.schema.RoleEntity
import core.database.schema.TextMessageEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TextMessageRepositoryImpl : TextMessageRepository {
    override suspend fun addToDatabase(message: TextMessageModel) = TextMessageAPIs.add(
        TextMessageEntity(
            participantsAddress = message.participantsAddress,
            message = message.message,
            timeStamp = message.timeStamp,
            deviceRole = if (message.deviceRole == TextMessageModelRole.Sender) RoleEntity.Sender else RoleEntity.Receiver
        )
    )

    override fun observerConversation(particpantAddress:String): Flow<List<TextMessageModel>> {
        //TODO ::Right now Scanned Device name has problem,that is why do not using the filter as:TextMessageAPIs.retrieveConversation(participantAddress)
        //TODO:Later fix it,from wifi direct client,otherwise all participant chat will be show in a single inbox
        return TextMessageAPIs.retrieveConversation().map { messages ->
            messages
                .map {
                TextMessageModel(
                    participantsAddress = it.participantsAddress,//the device itself is the receiver
                    message = it.message,
                    timeStamp = it.timeStamp,
                    deviceRole = if (it.deviceRole == RoleEntity.Sender) TextMessageModelRole.Sender else TextMessageModelRole.Receiver,
                )
            }
        }
    }

}