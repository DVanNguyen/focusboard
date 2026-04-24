package com.example.focusboard.data.remote.dto

import com.google.gson.annotations.SerializedName

data class PageDto(
    @SerializedName("id") val id: String,
    @SerializedName("workspace_id") val workspaceId: String,
    @SerializedName("title") val title: String,
    @SerializedName("icon_emoji") val iconEmoji: String? = null,
    @SerializedName("is_pinned") val isPinned: Boolean? = null,
    @SerializedName("position") val position: Int? = null,
    @SerializedName("updated_at_ms") val updatedAtMs: Long? = null,
    @SerializedName("is_deleted") val isDeleted: Boolean? = null,
)

data class PageRequest(
    @SerializedName("id") val id: String? = null,
    @SerializedName("workspace_id") val workspaceId: String? = null,
    @SerializedName("title") val title: String,
    @SerializedName("icon_emoji") val iconEmoji: String? = null,
    @SerializedName("is_pinned") val isPinned: Boolean? = null,
    @SerializedName("position") val position: Int? = null,
    @SerializedName("updated_at_ms") val updatedAtMs: Long? = null,
    @SerializedName("is_deleted") val isDeleted: Boolean? = null,
)

