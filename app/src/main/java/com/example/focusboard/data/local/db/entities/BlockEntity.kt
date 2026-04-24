package com.example.focusboard.data.local.db.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.focusboard.data.local.db.SyncStatus
import java.util.UUID

@Entity(
    tableName = "blocks",
    foreignKeys = [
        ForeignKey(
            entity = PageEntity::class,
            parentColumns = ["id"],
            childColumns = ["pageId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index("pageId", "position"),
        Index("syncStatus"),
    ],
)
data class BlockEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val pageId: String,
    val type: String, // H1|H2|TEXT|TODO|IMAGE|CODE|DIVIDER
    val content: String = "",
    val position: Int,
    val isChecked: Boolean = false,
    val metadata: String? = null,
    val syncStatus: SyncStatus = SyncStatus.PENDING,
    val updatedAt: Long = System.currentTimeMillis(),
    val isDeleted: Boolean = false,
)

object BlockType {
    const val H1 = "H1"
    const val H2 = "H2"
    const val TEXT = "TEXT"
    const val TODO = "TODO"
    const val IMAGE = "IMAGE"
    const val CODE = "CODE"
    const val DIVIDER = "DIVIDER"
}

