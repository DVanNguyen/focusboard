package com.example.focusboard.data.sync

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.focusboard.data.repository.BlockRepository
import com.example.focusboard.data.repository.PageRepository
import com.example.focusboard.data.repository.WorkspaceRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/**
 * WorkManager Worker đồng bộ dữ liệu PENDING lên server.
 */
@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val workspaceRepo: WorkspaceRepository,
    private val pageRepo: PageRepository,
    private val blockRepo: BlockRepository,
) : CoroutineWorker(context, params) {

    companion object {
        const val TAG = "SyncWorker"
    }

    override suspend fun doWork(): Result {
        return try {
            Log.d(TAG, "Starting sync…")

            // Push local pending changes lên server
            workspaceRepo.pushPendingToServer()
            pageRepo.pushPendingToServer()
            blockRepo.pushPendingToServer()

            // Pull latest data từ server
            workspaceRepo.pullFromServer()

            Log.d(TAG, "Sync completed successfully")
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Sync failed, will retry", e)
            Result.retry()
        }
    }
}
