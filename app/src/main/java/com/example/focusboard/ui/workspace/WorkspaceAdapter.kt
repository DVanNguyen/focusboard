package com.example.focusboard.ui.workspace

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.focusboard.data.local.db.entities.WorkspaceEntity
import com.example.focusboard.databinding.ItemWorkspaceBinding

class WorkspaceAdapter(
    private val onClick: (workspaceId: String) -> Unit,
) : ListAdapter<WorkspaceEntity, WorkspaceAdapter.VH>(Diff) {
    object Diff : DiffUtil.ItemCallback<WorkspaceEntity>() {
        override fun areItemsTheSame(oldItem: WorkspaceEntity, newItem: WorkspaceEntity) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: WorkspaceEntity, newItem: WorkspaceEntity) = oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemWorkspaceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding, onClick)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    class VH(
        private val binding: ItemWorkspaceBinding,
        private val onClick: (workspaceId: String) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: WorkspaceEntity) {
            binding.name.text = item.name
            binding.icon.text = item.iconEmoji.ifBlank { "📁" }
            binding.root.setOnClickListener { onClick(item.id) }
        }
    }
}

