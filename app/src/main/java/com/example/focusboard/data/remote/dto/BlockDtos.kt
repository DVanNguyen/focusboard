package com.example.focusboard.data.remote.dto

import com.google.gson.annotations.SerializedName

data class BlockDto(
    @SerializedName("id") val id: String,
    @SerializedName("page_id") val pageId: String,
    @SerializedName("type") val type: String,
    @SerializedName("content") val content: String? = null,
    @SerializedName("position") val position: Int,
    @SerializedName("is_checked") val isChecked: Boolean? = null,
    @SerializedName("metadata") val metadata: Map<String, Any>? = null,
    @SerializedName("updated_at_ms") val updatedAtMs: Long? = null,
    @SerializedName("is_deleted") val isDeleted: Boolean? = null,
)

data class BlockRequest(
    @SerializedName("id") val id: String? = null,
    @SerializedName("page_id") val pageId: String? = null,
    @SerializedName("type") val type: String,
    @SerializedName("content") val content: String? = null,
    @SerializedName("position") val position: Int? = null,
    @SerializedName("is_checked") val isChecked: Boolean? = null,
    @SerializedName("metadata") val metadata: Map<String, Any>? = null,
    @SerializedName("updated_at_ms") val updatedAtMs: Long? = null,
    @SerializedName("is_deleted") val isDeleted: Boolean? = null,
)

data class ReorderBlocksRequest(
    @SerializedName("positions") val positions: List<ReorderBlockItem>,
)

data class ReorderBlockItem(
    @SerializedName("id") val id: String,
    @SerializedName("position") val position: Int,
    @SerializedName("updated_at_ms") val updatedAtMs: Long? = null,
)

