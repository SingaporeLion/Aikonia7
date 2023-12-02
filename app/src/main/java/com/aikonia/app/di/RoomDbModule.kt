package com.aikonia.app.di

import android.content.Context
import androidx.room.Room
import com.aikonia.app.data.source.local.ConversAIDatabase
import com.aikonia.app.data.source.local.UserDao
import com.aikonia.app.data.source.local.UserRepository
import com.aikonia.app.data.source.local.UserRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RoomDbModule {

    @Provides
    @Singleton
    fun provideRoomDb(@ApplicationContext appContext: Context): ConversAIDatabase =
        Room.databaseBuilder(
            appContext,
            ConversAIDatabase::class.java,
            "conversAIdb.db"
        ).build()

    @Provides
    @Singleton
    fun provideConversAIDao(conversAIDatabase: ConversAIDatabase) = conversAIDatabase.conversAIDao()

    // Neue Provides-Methode für UserDao
    @Provides
    @Singleton
    fun provideUserDao(conversAIDatabase: ConversAIDatabase): UserDao = conversAIDatabase.userDao()

    // Neue Provides-Methode für UserRepository
    @Provides
    @Singleton
    fun provideUserRepository(userDao: UserDao): UserRepository = UserRepositoryImpl(userDao)
}
