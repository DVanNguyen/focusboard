package com.example.focusboard.ui.page

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.example.focusboard.data.local.db.entities.BlockEntity
import com.example.focusboard.data.local.db.entities.BlockType
import com.example.focusboard.data.repository.BlockRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PageDetailViewModel @Inject constructor(
    private val repo: BlockRepository,
) : ViewModel() {
    private val pageId = MutableLiveData<String>()

    val blocks: LiveData<List<BlockEntity>> =
        pageId.switchMap { id -> repo.observeBlocks(id).asLiveData() }

    fun bind(pageId: String) {
        if (this.pageId.value == pageId) return
        this.pageId.value = pageId
    }

    fun addBlock(type: String) {
        val pid = pageId.value ?: return
        viewModelScope.launch {
            val current = blocks.value.orEmpty()
            repo.createBlock(
                pageId = pid,
                type = type,
                content = when (type) {
                    BlockType.H1, BlockType.H2 -> "Header"
                    BlockType.TODO -> ""
                    else -> ""
                },
                position = current.size,
            )
        }
    }

    fun deleteBlock(blockId: String) {
        viewModelScope.launch { repo.deleteBlock(blockId) }
    }

    fun saveNewOrder(newOrder: List<BlockEntity>) {
        viewModelScope.launch { repo.saveNewBlockOrder(newOrder) }
    }

    fun updateContent(blockId: String, content: String) {
        val item = blocks.value?.firstOrNull { it.id == blockId } ?: return
        if (item.content == content) return
        viewModelScope.launch { repo.updateBlock(item.copy(content = content)) }
    }

    fun updateChecked(blockId: String, checked: Boolean) {
        val item = blocks.value?.firstOrNull { it.id == blockId } ?: return
        if (item.isChecked == checked) return
        viewModelScope.launch { repo.updateBlock(item.copy(isChecked = checked)) }
    }
}

