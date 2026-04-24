package com.example.focusboard.ui.tasks

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.focusboard.data.local.db.entities.TaskItem
import com.example.focusboard.databinding.ItemTaskBinding

class TaskAdapter(
    private val onCheckedChanged: (blockId: String, isChecked: Boolean) -> Unit,
) : ListAdapter<TaskItem, TaskAdapter.VH>(Diff) {

    object Diff : DiffUtil.ItemCallback<TaskItem>() {
        override fun areItemsTheSame(oldItem: TaskItem, newItem: TaskItem) = oldItem.blockId == newItem.blockId
        override fun areContentsTheSame(oldItem: TaskItem, newItem: TaskItem) = oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding, onCheckedChanged)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    class VH(
        private val binding: ItemTaskBinding,
        private val onCheckedChanged: (blockId: String, isChecked: Boolean) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {

        private var currentBlockId: String? = null

        init {
            binding.checkbox.setOnCheckedChangeListener { _, isChecked ->
                val id = currentBlockId ?: return@setOnCheckedChangeListener
                onCheckedChanged(id, isChecked)
            }
        }

        fun bind(item: TaskItem) {
            currentBlockId = null // Prevent listener trigger during bind
            binding.checkbox.isChecked = item.isChecked
            currentBlockId = item.blockId

            binding.content.text = item.content.ifBlank { "Untitled task" }
            binding.pageInfo.text = "${item.workspaceName} › ${item.pageTitle}"

            // Strikethrough cho task đã hoàn thành
            binding.content.paintFlags = if (item.isChecked) {
                binding.content.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                binding.content.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }

            binding.content.alpha = if (item.isChecked) 0.5f else 1.0f
        }
    }
}
