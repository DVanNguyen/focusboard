package com.example.focusboard.data.repository

import com.example.focusboard.data.local.db.SyncStatus
import com.example.focusboard.data.local.db.dao.BlockDao
import com.example.focusboard.data.local.db.entities.BlockEntity
import com.example.focusboard.data.remote.api.FocusBoardApiService
import com.example.focusboard.data.remote.dto.ReorderBlockItem
import com.example.focusboard.data.remote.dto.ReorderBlocksRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BlockRepository @Inject constructor(
    private val dao: BlockDao,
    private val api: FocusBoardApiService,
) {
    fun observeBlocks(pageId: String): Flow<List<BlockEntity>> = dao.observeBlocks(pageId)

    suspend fun createBlock(
        pageId: String,
        type: String,
        content: String,
        position: Int,
    ): BlockEntity = withContext(Dispatchers.IO) {
        val entity = BlockEntity(
            pageId = pageId,
            type = type,
            content = content,
            position = position,
            syncStatus = SyncStatus.PENDING,
        )
        dao.upsert(entity)
        entity
    }

    suspend fun updateBlock(entity: BlockEntity) = withContext(Dispatchers.IO) {
        dao.upsert(entity.markPending())
    }

    suspend fun deleteBlock(id: String) = withContext(Dispatchers.IO) {
        dao.softDelete(id)
    }

    suspend fun saveNewBlockOrder(blocksInNewOrder: List<BlockEntity>) = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        val updated = blocksInNewOrder.mapIndexed { index, b ->
            b.copy(position = index, syncStatus = SyncStatus.PENDING, updatedAt = now)
        }
        dao.replacePositions(updated)
    }

    /**
     * Pull blocks cho page từ server.
     */
    suspend fun pullFromServer(pageId: String) = withContext(Dispatchers.IO) {
        try {
            val res = api.getBlocks(pageId)
            val dtos = res.body()?.data ?: return@withContext
            val entities = dtos.map { it.toEntity() }
            dao.upsertAll(entities)
        } catch (_: Exception) {
            // Offline — ignore
        }
    }

    suspend fun pushPendingToServer() = withContext(Dispatchers.IO) {
        val pendings = dao.getBySyncStatus(SyncStatus.PENDING)
        pendings.forEach { block ->
            try {
                val res = if (block.isDeleted) {
                    api.deleteBlock(block.id)
                    null
                } else {
                    val updateRes = api.updateBlock(block.id, block.toRequest())
                    if (!updateRes.isSuccessful && updateRes.code() == 404) {
                        api.createBlock(block.pageId, block.toRequest())
                    } else {
                        updateRes
                    }
                }

                val ok = (res == null) || res.isSuccessful
                if (ok) {
                    dao.upsert(block.copy(syncStatus = SyncStatus.SYNCED))
                }
            } catch (_: Exception) {
                // Keep pending
            }
        }
    }

    suspend fun pushReorderToServer(pageId: String, blocksInNewOrder: List<BlockEntity>) = withContext(Dispatchers.IO) {
        val body = ReorderBlocksRequest(
            positions = blocksInNewOrder.mapIndexed { index, b ->
                ReorderBlockItem(id = b.id, position = index, updatedAtMs = b.updatedAt)
            },
        )
        try {
            api.reorderBlocks(pageId, body)
        } catch (_: Exception) {
            // ignore, will retry later via generic pending push
        }
    }
}
