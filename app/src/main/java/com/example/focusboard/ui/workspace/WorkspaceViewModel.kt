package com.example.focusboard.ui.workspace

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.focusboard.data.local.db.entities.WorkspaceEntity
import com.example.focusboard.data.repository.WorkspaceRepository
import com.example.focusboard.data.sync.SyncScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkspaceViewModel @Inject constructor(
    private val repo: WorkspaceRepository,
    private val syncScheduler: SyncScheduler,
) : ViewModel() {
    val workspaces: LiveData<List<WorkspaceEntity>> = repo.observeWorkspaces().asLiveData()

    private val _isLoading = MutableLiveData(true)
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        refreshFromServer()
    }

    private fun refreshFromServer() {
        viewModelScope.launch {
            _isLoading.value = true
            repo.pullFromServer()
            _isLoading.value = false
        }
    }

    fun quickCreateWorkspace() {
        viewModelScope.launch {
            val current = workspaces.value.orEmpty()
            repo.createWorkspace(
                name = "Workspace ${current.size + 1}",
                iconEmoji = "📌",
                color = "#1A56DB",
                position = current.size,
            )
            syncScheduler.syncNow()
        }
    }
}
