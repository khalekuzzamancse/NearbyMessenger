package chat.di

import chat.data.TextMessageRepositoryImpl
import chat.domain.reposiory.TextMessageRepository

object DependencyFactory {
    fun getChatRepository():TextMessageRepository{
        return  TextMessageRepositoryImpl()
    }
}