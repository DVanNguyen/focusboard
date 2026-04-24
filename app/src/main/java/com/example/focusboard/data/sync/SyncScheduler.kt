package com.example.focusboard.data.sync

import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Quản lý lịch trình sync.
 * - schedulePeriodic(): đăng ký sync mỗi 15 phút khi có mạng
 * - syncNow(): enqueue sync ngay lập tức (sau create/update/delete)
 */
@Singleton
class SyncScheduler @Inject constructor(
    private val workManager: WorkManager,
) {
    companion object {
        private const val PERIODIC_WORK_NAME = "focusboard_periodic_sync"
        private const val ONE_TIME_WORK_NAME = "focusboard_immediate_sync"
    }

    /**
     * Đăng ký sync định kỳ mỗi 15 phút.
     * Gọi 1 lần trong Application.onCreate().
     */
    fun schedulePeriodic() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request = PeriodicWorkRequestBuilder<SyncWorker>(
            15, TimeUnit.MINUTES,
        )
            .setConstraints(constraints)
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30, TimeUnit.SECONDS)
            .build()

        workManager.enqueueUniquePeriodicWork(
            PERIODIC_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            request,
        )
    }

    /**
     * Enqueue sync ngay lập tức.
     * Gọi sau mỗi thao tác write (create/update/delete).
     */
    fun syncNow() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(constraints)
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 10, TimeUnit.SECONDS)
            .build()

        workManager.enqueueUniqueWork(
            ONE_TIME_WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            request,
        )
    }
}
