package core.database.api

import core.database.DB
import core.database.DB.addEntity
import core.database.DB.retrieveEntities
import core.database.schema.RoleEntity
import core.database.schema.TextMessageEntity
import core.database.schema.TextMessageSchema
import io.realm.kotlin.ext.query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object TextMessageAPIs {
    private val db = DB.db
    suspend fun add(entity: TextMessageEntity): Result<Unit> {
        val result: Result<TextMessageEntity> = addEntity(TextMessageSchema().apply {
            this.participantsAddress=entity.participantName
            this.message = entity.message
            this.timeStamp = entity.timeStamp
            this.isSender=entity.deviceRole==RoleEntity.Sender
        }) { this.toEntity() }
        return if (result.isSuccess)
            Result.success(Unit)
        else
            Result.failure(
                result.exceptionOrNull() ?: Throwable("Failed to add database for unknown reason")
            )
    }


    fun retrieveConversation(participantAddress: String): Flow<List<TextMessageEntity>> {
        val original= retrieveConversation()
        val filtered=original.map { list ->
            list.filter {message-> message.participantName==participantAddress }
        }
        return filtered
    }

    fun retrieveConversation(): Flow<List<TextMessageEntity>> {
        return getAll().map { list -> list.map { it.toEntity() } }
    }

    private fun getAll(): Flow<List<TextMessageSchema>> {
        return DB.db.query<TextMessageSchema>().asFlow().map { it.list }
    }

    private fun retrieveText(receiverAddress: String): Result<List<TextMessageEntity>> {
        val res: Result<List<TextMessageEntity>> = retrieveEntities(
            query = {
                db.query<TextMessageSchema>("receiverDeviceAddress=$0", receiverAddress).find()
            },
            transform = TextMessageSchema::toEntity
        )
        return res
    }
    private fun log(message: String, methodName: String? = null) {
        val tag = "${this@TextMessageAPIs::class.simpleName}Log:"
        val method = if (methodName == null) "" else "$methodName()'s "
        println("$tag:$method:-> $message")
    }
}
