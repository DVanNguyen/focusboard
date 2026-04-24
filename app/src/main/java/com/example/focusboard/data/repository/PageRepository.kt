package com.example.focusboard.data.repository

import com.example.focusboard.data.local.db.SyncStatus
import com.example.focusboard.data.local.db.dao.PageDao
import com.example.focusboard.data.local.db.entities.PageEntity
import com.example.focusboard.data.remote.api.FocusBoardApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PageRepository @Inject constructor(
    private val dao: PageDao,
    private val api: FocusBoardApiService,
) {
    fun observePages(workspaceId: String): Flow<List<PageEntity>> = dao.observePages(workspaceId)

    suspend fun createPage(
        workspaceId: String,
        title: String,
        iconEmoji: String,
        position: Int,
    ): PageEntity = withContext(Dispatchers.IO) {
        val entity = PageEntity(
            workspaceId = workspaceId,
            title = title,
            iconEmoji = iconEmoji,
            position = position,
            syncStatus = SyncStatus.PENDING,
        )
        dao.upsert(entity)
        entity
    }

    suspend fun updatePage(entity: PageEntity) = withContext(Dispatchers.IO) {
        dao.upsert(entity.markPending())
    }

    suspend fun deletePage(id: String) = withContext(Dispatchers.IO) {
        dao.softDelete(id)
    }

    /**
     * Pull pages cho workspace từ server.
     */
    suspend fun pullFromServer(workspaceId: String) = withContext(Dispatchers.IO) {
        try {
            val res = api.getPages(workspaceId)
            val dtos = res.body()?.data ?: return@withContext
            val entities = dtos.map { it.toEntity() }
            dao.upsertAll(entities)
        } catch (_: Exception) {
            // Offline — ignore
        }
    }

    suspend fun pushPendingToServer() = withContext(Dispatchers.IO) {
        val pendings = dao.getBySyncStatus(SyncStatus.PENDING)
        pendings.forEach { page ->
            try {
                val res = if (page.isDeleted) {
                    api.deletePage(page.id)
                    null
                } else {
                    val updateRes = api.updatePage(page.id, page.toRequest())
                    if (!updateRes.isSuccessful && updateRes.code() == 404) {
                        api.createPage(page.workspaceId, page.toRequest())
                    } else {
                        updateRes
                    }
                }

                val ok = (res == null) || res.isSuccessful
                if (ok) {
                    dao.upsert(page.copy(syncStatus = SyncStatus.SYNCED))
                }
            } catch (_: Exception) {
                // Keep pending
            }
        }
    }
}
