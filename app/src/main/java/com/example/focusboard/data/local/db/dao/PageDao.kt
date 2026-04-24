package com.example.focusboard.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.focusboard.data.local.db.SyncStatus
import com.example.focusboard.data.local.db.entities.PageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PageDao {
    @Query(
        "SELECT * FROM pages WHERE workspaceId = :workspaceId AND isDeleted = 0 " +
            "ORDER BY isPinned DESC, position ASC, updatedAt DESC",
    )
    fun observePages(workspaceId: String): Flow<List<PageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(page: PageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(pages: List<PageEntity>)

    @Update
    suspend fun update(page: PageEntity)

    @Query("UPDATE pages SET isDeleted = 1, syncStatus = :syncStatus, updatedAt = :updatedAt WHERE id = :id")
    suspend fun softDelete(id: String, syncStatus: SyncStatus = SyncStatus.PENDING, updatedAt: Long = System.currentTimeMillis())

    @Query("SELECT * FROM pages WHERE syncStatus = :status")
    suspend fun getBySyncStatus(status: SyncStatus = SyncStatus.PENDING): List<PageEntity>
}

