package com.example.focusboard.data.local.db.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.focusboard.data.local.db.SyncStatus
import java.util.UUID

@Entity(
    tableName = "pages",
    foreignKeys = [
        ForeignKey(
            entity = WorkspaceEntity::class,
            parentColumns = ["id"],
            childColumns = ["workspaceId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index("workspaceId", "position"),
        Index("syncStatus"),
    ],
)
data class PageEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val workspaceId: String,
    val title: String,
    val iconEmoji: String = "",
    val isPinned: Boolean = false,
    val position: Int = 0,
    val syncStatus: SyncStatus = SyncStatus.PENDING,
    val updatedAt: Long = System.currentTimeMillis(),
    val isDeleted: Boolean = false,
)

