package com.example.focusboard.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.focusboard.data.local.db.SyncStatus
import com.example.focusboard.data.local.db.entities.WorkspaceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkspaceDao {
    @Query("SELECT * FROM workspaces WHERE isDeleted = 0 ORDER BY position ASC, updatedAt DESC")
    fun observeWorkspaces(): Flow<List<WorkspaceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(workspace: WorkspaceEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(workspaces: List<WorkspaceEntity>)

    @Update
    suspend fun update(workspace: WorkspaceEntity)

    @Query("UPDATE workspaces SET isDeleted = 1, syncStatus = :syncStatus, updatedAt = :updatedAt WHERE id = :id")
    suspend fun softDelete(id: String, syncStatus: SyncStatus = SyncStatus.PENDING, updatedAt: Long = System.currentTimeMillis())

    @Query("SELECT * FROM workspaces WHERE syncStatus = :status")
    suspend fun getBySyncStatus(status: SyncStatus = SyncStatus.PENDING): List<WorkspaceEntity>
}

