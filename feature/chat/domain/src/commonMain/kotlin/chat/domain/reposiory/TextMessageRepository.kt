package chat.domain.reposiory

import chat.domain.model.TextMessageModel
import kotlinx.coroutines.flow.Flow

interface TextMessageRepository {
    suspend fun addToDatabase(message: TextMessageModel):Result<Unit>

    /**
     * Using function instead of variable so that later filter argument injected
     */
   fun observerGroupConversation(): Flow<List<TextMessageModel>>
}