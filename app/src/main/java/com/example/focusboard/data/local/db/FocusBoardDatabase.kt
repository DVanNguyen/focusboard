package com.example.focusboard.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.focusboard.data.local.db.dao.BlockDao
import com.example.focusboard.data.local.db.dao.PageDao
import com.example.focusboard.data.local.db.dao.WorkspaceDao
import com.example.focusboard.data.local.db.entities.BlockEntity
import com.example.focusboard.data.local.db.entities.PageEntity
import com.example.focusboard.data.local.db.entities.UserEntity
import com.example.focusboard.data.local.db.entities.WorkspaceEntity

@Database(
    entities = [
        UserEntity::class,
        WorkspaceEntity::class,
        PageEntity::class,
        BlockEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
@TypeConverters(RoomConverters::class)
abstract class FocusBoardDatabase : RoomDatabase() {
    abstract fun workspaceDao(): WorkspaceDao
    abstract fun pageDao(): PageDao
    abstract fun blockDao(): BlockDao
}

