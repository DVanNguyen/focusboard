package com.example.focusboard.data.remote.dto

import com.google.gson.annotations.SerializedName

data class WorkspaceDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("icon_emoji") val iconEmoji: String? = null,
    @SerializedName("color") val color: String? = null,
    @SerializedName("position") val position: Int? = null,
    @SerializedName("updated_at_ms") val updatedAtMs: Long? = null,
    @SerializedName("is_deleted") val isDeleted: Boolean? = null,
)

data class WorkspaceRequest(
    @SerializedName("id") val id: String? = null,
    @SerializedName("name") val name: String,
    @SerializedName("icon_emoji") val iconEmoji: String? = null,
    @SerializedName("color") val color: String? = null,
    @SerializedName("position") val position: Int? = null,
    @SerializedName("updated_at_ms") val updatedAtMs: Long? = null,
    @SerializedName("is_deleted") val isDeleted: Boolean? = null,
)

