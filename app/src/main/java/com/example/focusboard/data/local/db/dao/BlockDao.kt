package com.example.focusboard.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.focusboard.data.local.db.SyncStatus
import com.example.focusboard.data.local.db.entities.BlockEntity
import com.example.focusboard.data.local.db.entities.TaskItem
import kotlinx.coroutines.flow.Flow

@Dao
interface BlockDao {
    @Query("SELECT * FROM blocks WHERE pageId = :pageId AND isDeleted = 0 ORDER BY position ASC")
    fun observeBlocks(pageId: String): Flow<List<BlockEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(block: BlockEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(blocks: List<BlockEntity>)

    @Update
    suspend fun update(block: BlockEntity)

    @Query("UPDATE blocks SET isDeleted = 1, syncStatus = :syncStatus, updatedAt = :updatedAt WHERE id = :id")
    suspend fun softDelete(id: String, syncStatus: SyncStatus = SyncStatus.PENDING, updatedAt: Long = System.currentTimeMillis())

    @Transaction
    suspend fun replacePositions(blocks: List<BlockEntity>) {
        blocks.forEach { update(it) }
    }

    @Query("SELECT * FROM blocks WHERE syncStatus = :status")
    suspend fun getBySyncStatus(status: SyncStatus = SyncStatus.PENDING): List<BlockEntity>

    /**
     * Tổng hợp tất cả TODO blocks (chưa xóa) kèm thông tin page + workspace.
     * Dùng cho Task List screen.
     */
    @Query(
        """
        SELECT b.id AS blockId, b.content, b.isChecked, b.pageId,
               p.title AS pageTitle, w.name AS workspaceName, b.updatedAt
        FROM blocks b
        INNER JOIN pages p ON b.pageId = p.id
        INNER JOIN workspaces w ON p.workspaceId = w.id
        WHERE b.type = 'TODO' AND b.isDeleted = 0 AND p.isDeleted = 0 AND w.isDeleted = 0
        ORDER BY b.isChecked ASC, b.updatedAt DESC
        """,
    )
    fun observeAllTodos(): Flow<List<TaskItem>>

    /**
     * Chỉ lấy TODO chưa hoàn thành.
     */
    @Query(
        """
        SELECT b.id AS blockId, b.content, b.isChecked, b.pageId,
               p.title AS pageTitle, w.name AS workspaceName, b.updatedAt
        FROM blocks b
        INNER JOIN pages p ON b.pageId = p.id
        INNER JOIN workspaces w ON p.workspaceId = w.id
        WHERE b.type = 'TODO' AND b.isDeleted = 0 AND p.isDeleted = 0 AND w.isDeleted = 0
              AND b.isChecked = 0
        ORDER BY b.updatedAt DESC
        """,
    )
    fun observeUnfinishedTodos(): Flow<List<TaskItem>>

    /**
     * Update isChecked trực tiếp cho một block (dùng trong Task List).
     */
    @Query("UPDATE blocks SET isChecked = :checked, syncStatus = 'PENDING', updatedAt = :updatedAt WHERE id = :blockId")
    suspend fun updateChecked(blockId: String, checked: Boolean, updatedAt: Long = System.currentTimeMillis())
}
