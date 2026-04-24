package com.example.focusboard.data.local.db.entities

/**
 * POJO trả về từ JOIN query giữa blocks + pages + workspaces.
 * Dùng cho Task List screen — tổng hợp tất cả TODO blocks.
 */
data class TaskItem(
    val blockId: String,
    val content: String,
    val isChecked: Boolean,
    val pageId: String,
    val pageTitle: String,
    val workspaceName: String,
    val updatedAt: Long,
)
