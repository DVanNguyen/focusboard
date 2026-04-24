package com.example.focusboard.data.repository

import com.example.focusboard.data.local.db.SyncStatus
import com.example.focusboard.data.local.db.dao.WorkspaceDao
import com.example.focusboard.data.local.db.entities.WorkspaceEntity
import com.example.focusboard.data.remote.api.FocusBoardApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkspaceRepository @Inject constructor(
    private val dao: WorkspaceDao,
    private val api: FocusBoardApiService,
) {
    fun observeWorkspaces(): Flow<List<WorkspaceEntity>> = dao.observeWorkspaces()

    suspend fun createWorkspace(
        name: String,
        iconEmoji: String,
        color: String,
        position: Int,
    ): WorkspaceEntity = withContext(Dispatchers.IO) {
        val entity = WorkspaceEntity(
            name = name,
            iconEmoji = iconEmoji,
            color = color,
            position = position,
            syncStatus = SyncStatus.PENDING,
        )
        dao.upsert(entity)
        entity
    }

    suspend fun updateWorkspace(entity: WorkspaceEntity) = withContext(Dispatchers.IO) {
        dao.upsert(entity.markPending())
    }

    suspend fun deleteWorkspace(id: String) = withContext(Dispatchers.IO) {
        dao.softDelete(id)
    }

    /**
     * Pull workspaces từ server rồi merge vào Room.
     * Chỉ upsert nếu server version mới hơn (hoặc chưa có local).
     */
    suspend fun pullFromServer() = withContext(Dispatchers.IO) {
        try {
            val res = api.getWorkspaces()
            val dtos = res.body()?.data ?: return@withContext
            val entities = dtos.map { it.toEntity() }
            dao.upsertAll(entities)
        } catch (_: Exception) {
            // Offline — bỏ qua, dùng dữ liệu local
        }
    }

    /**
     * Push bản ghi PENDING lên server.
     * SyncWorker gọi hàm này.
     */
    suspend fun pushPendingToServer() = withContext(Dispatchers.IO) {
        val pendings = dao.getBySyncStatus(SyncStatus.PENDING)
        pendings.forEach { ws ->
            try {
                val res = if (ws.isDeleted) {
                    api.deleteWorkspace(ws.id)
                    null
                } else {
                    // Thử update trước, nếu 404 thì create
                    val updateRes = api.updateWorkspace(ws.id, ws.toRequest())
                    if (!updateRes.isSuccessful && updateRes.code() == 404) {
                        api.createWorkspace(ws.toRequest())
                    } else {
                        updateRes
                    }
                }

                val ok = (res == null) || res.isSuccessful
                if (ok) {
                    dao.upsert(ws.copy(syncStatus = SyncStatus.SYNCED))
                }
            } catch (_: Exception) {
                // Keep pending, retry later
            }
        }
    }
}
