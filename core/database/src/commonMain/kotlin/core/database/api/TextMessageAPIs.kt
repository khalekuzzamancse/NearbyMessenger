package core.database.api

import core.database.DB
import core.database.DB.addEntity
import core.database.DB.retrieveEntities
import core.database.schema.TextMessageEntity
import core.database.schema.TextMessageSchema
import io.realm.kotlin.ext.query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object TextMessageAPIs {

    private val db = DB.db


    suspend fun addTextMessage(model: TextMessageEntity) =
        addEntity(TextMessageSchema().apply {
            this.senderDeviceAddress = model.senderDeviceAddress
            this.message = model.message
            this.timeStamp = model.timeStamp
        }) { this.toEntity() }




    fun getMessages(senderAddress: String): Flow<List<TextMessageEntity>> {
        return getMessages() // Assuming getMessages() returns Flow<List<TextMessageEntity>>
            .map { list ->
                list.filter {
                    it.senderDeviceAddress == senderAddress
                }
            }
    }
     fun getMessages(): Flow<List<TextMessageEntity>> {
        return getAllMessages().map { list -> list.map { it.toEntity() } }
    }

    private fun getAllMessages(): Flow<List<TextMessageSchema>> {
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
}
