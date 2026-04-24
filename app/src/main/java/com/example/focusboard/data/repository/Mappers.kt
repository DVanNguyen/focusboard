package com.example.focusboard.data.repository

import com.example.focusboard.data.local.db.SyncStatus
import com.example.focusboard.data.local.db.entities.BlockEntity
import com.example.focusboard.data.local.db.entities.PageEntity
import com.example.focusboard.data.local.db.entities.WorkspaceEntity
import com.example.focusboard.data.remote.dto.BlockDto
import com.example.focusboard.data.remote.dto.BlockRequest
import com.example.focusboard.data.remote.dto.PageDto
import com.example.focusboard.data.remote.dto.PageRequest
import com.example.focusboard.data.remote.dto.WorkspaceDto
import com.example.focusboard.data.remote.dto.WorkspaceRequest
import com.google.gson.Gson

// ── Entity → Request (push lên server) ─────────────────────────

internal fun WorkspaceEntity.toRequest(): WorkspaceRequest =
    WorkspaceRequest(
        id = id,
        name = name,
        iconEmoji = iconEmoji,
        color = color,
        position = position,
        updatedAtMs = updatedAt,
        isDeleted = isDeleted,
    )

internal fun PageEntity.toRequest(): PageRequest =
    PageRequest(
        id = id,
        workspaceId = workspaceId,
        title = title,
        iconEmoji = iconEmoji,
        isPinned = isPinned,
        position = position,
        updatedAtMs = updatedAt,
        isDeleted = isDeleted,
    )

internal fun BlockEntity.toRequest(): BlockRequest {
    val metadataMap = metadata?.let {
        try {
            Gson().fromJson(it, Map::class.java) as Map<String, Any>
        } catch (_: Exception) {
            null
        }
    }
    return BlockRequest(
        id = id,
        pageId = pageId,
        type = type,
        content = content,
        position = position,
        isChecked = isChecked,
        metadata = metadataMap,
        updatedAtMs = updatedAt,
        isDeleted = isDeleted,
    )
}

// ── DTO → Entity (pull từ server) ──────────────────────────────

internal fun WorkspaceDto.toEntity(): WorkspaceEntity =
    WorkspaceEntity(
        id = id,
        name = name,
        iconEmoji = iconEmoji.orEmpty(),
        color = color ?: "#1A56DB",
        position = position ?: 0,
        syncStatus = SyncStatus.SYNCED,
        updatedAt = updatedAtMs ?: System.currentTimeMillis(),
        isDeleted = isDeleted ?: false,
    )

internal fun PageDto.toEntity(): PageEntity =
    PageEntity(
        id = id,
        workspaceId = workspaceId,
        title = title,
        iconEmoji = iconEmoji.orEmpty(),
        isPinned = isPinned ?: false,
        position = position ?: 0,
        syncStatus = SyncStatus.SYNCED,
        updatedAt = updatedAtMs ?: System.currentTimeMillis(),
        isDeleted = isDeleted ?: false,
    )

internal fun BlockDto.toEntity(): BlockEntity =
    BlockEntity(
        id = id,
        pageId = pageId,
        type = type,
        content = content.orEmpty(),
        position = position,
        isChecked = isChecked ?: false,
        metadata = metadata?.let { Gson().toJson(it) },
        syncStatus = SyncStatus.SYNCED,
        updatedAt = updatedAtMs ?: System.currentTimeMillis(),
        isDeleted = isDeleted ?: false,
    )

// ── markPending helpers ────────────────────────────────────────

internal fun WorkspaceEntity.markPending(now: Long = System.currentTimeMillis()): WorkspaceEntity =
    copy(syncStatus = SyncStatus.PENDING, updatedAt = now)

internal fun PageEntity.markPending(now: Long = System.currentTimeMillis()): PageEntity =
    copy(syncStatus = SyncStatus.PENDING, updatedAt = now)

internal fun BlockEntity.markPending(now: Long = System.currentTimeMillis()): BlockEntity =
    copy(syncStatus = SyncStatus.PENDING, updatedAt = now)
