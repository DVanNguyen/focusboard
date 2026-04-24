package com.example.focusboard.di

import android.content.Context
import androidx.room.Room
import com.example.focusboard.data.local.db.FocusBoardDatabase
import com.example.focusboard.data.local.db.dao.BlockDao
import com.example.focusboard.data.local.db.dao.PageDao
import com.example.focusboard.data.local.db.dao.WorkspaceDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): FocusBoardDatabase {
        return Room.databaseBuilder(
            context,
            FocusBoardDatabase::class.java,
            "focusboard.db",
        ).build()
    }

    @Provides
    fun provideWorkspaceDao(db: FocusBoardDatabase): WorkspaceDao = db.workspaceDao()

    @Provides
    fun providePageDao(db: FocusBoardDatabase): PageDao = db.pageDao()

    @Provides
    fun provideBlockDao(db: FocusBoardDatabase): BlockDao = db.blockDao()
}

