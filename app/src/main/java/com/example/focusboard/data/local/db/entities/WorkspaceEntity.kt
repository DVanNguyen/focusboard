package com.example.focusboard.data.local.db.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.focusboard.data.local.db.SyncStatus
import java.util.UUID

@Entity(
    tableName = "workspaces",
    indices = [
        Index("syncStatus"),
    ],
)
data class WorkspaceEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val iconEmoji: String = "",
    val color: String = "#1A56DB",
    val position: Int = 0,
    val syncStatus: SyncStatus = SyncStatus.PENDING,
    val updatedAt: Long = System.currentTimeMillis(),
    val isDeleted: Boolean = false,
)

