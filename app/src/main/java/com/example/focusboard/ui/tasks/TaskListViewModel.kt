package com.example.focusboard.ui.tasks

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.focusboard.data.local.db.dao.BlockDao
import com.example.focusboard.data.local.db.entities.TaskItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskListViewModel @Inject constructor(
    private val blockDao: BlockDao,
) : ViewModel() {

    enum class Filter { ALL, INCOMPLETE, COMPLETED }

    private val allTodos: LiveData<List<TaskItem>> = blockDao.observeAllTodos().asLiveData()
    private val _filter = MutableLiveData(Filter.ALL)
    val filter: LiveData<Filter> = _filter

    val tasks: LiveData<List<TaskItem>> = MediatorLiveData<List<TaskItem>>().apply {
        addSource(allTodos) { applyFilter(it, _filter.value ?: Filter.ALL, this) }
        addSource(_filter) { applyFilter(allTodos.value.orEmpty(), it, this) }
    }

    private fun applyFilter(
        list: List<TaskItem>,
        filter: Filter,
        target: MediatorLiveData<List<TaskItem>>,
    ) {
        target.value = when (filter) {
            Filter.ALL -> list
            Filter.INCOMPLETE -> list.filter { !it.isChecked }
            Filter.COMPLETED -> list.filter { it.isChecked }
        }
    }

    fun setFilter(filter: Filter) {
        _filter.value = filter
    }

    fun toggleTodo(blockId: String, isChecked: Boolean) {
        viewModelScope.launch {
            blockDao.updateChecked(blockId, isChecked)
        }
    }
}
