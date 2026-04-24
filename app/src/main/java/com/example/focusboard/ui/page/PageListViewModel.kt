package com.example.focusboard.ui.page

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.example.focusboard.data.local.db.entities.PageEntity
import com.example.focusboard.data.repository.PageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PageListViewModel @Inject constructor(
    private val repo: PageRepository,
) : ViewModel() {
    private val workspaceId = MutableLiveData<String>()
    private val query = MutableLiveData("")

    private val rawPages: LiveData<List<PageEntity>> =
        workspaceId.switchMap { wsId -> repo.observePages(wsId).asLiveData() }

    val pages: LiveData<List<PageEntity>> =
        rawPages.switchMap { list ->
            query.map { q ->
                val s = q.trim()
                if (s.isBlank()) list else list.filter { it.title.contains(s, ignoreCase = true) }
            }
        }

    fun bind(workspaceId: String) {
        if (this.workspaceId.value == workspaceId) return
        this.workspaceId.value = workspaceId
    }

    fun setQuery(value: String) {
        query.value = value
    }

    fun quickCreatePage() {
        val wsId = workspaceId.value ?: return
        viewModelScope.launch {
            val current = rawPages.value.orEmpty()
            repo.createPage(
                workspaceId = wsId,
                title = "Page ${current.size + 1}",
                iconEmoji = "📝",
                position = current.size,
            )
        }
    }
}

