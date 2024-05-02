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
            participantName = message.participantsName,
            message = message.message,
            timeStamp = message.timeStamp,
            deviceRole = if (message.deviceRole == TextMessageModelRole.Sender) RoleEntity.Sender else RoleEntity.Receiver
        )
    )


    override fun observerGroupConversation(): Flow<List<TextMessageModel>> {
        return TextMessageAPIs.retrieveConversation().map { messages ->
            messages.map {
                TextMessageModel(
                    participantsName = it.participantName,//the device itself is the receiver
                    message = it.message,
                    timeStamp = it.timeStamp,
                    deviceRole = if (it.deviceRole == RoleEntity.Sender) TextMessageModelRole.Sender else TextMessageModelRole.Receiver,
                )
            }
        }
    }

}