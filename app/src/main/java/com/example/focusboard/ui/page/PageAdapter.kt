package com.example.focusboard.ui.page

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.focusboard.data.local.db.entities.PageEntity
import com.example.focusboard.databinding.ItemPageBinding

class PageAdapter(
    private val onClick: (pageId: String) -> Unit,
) : ListAdapter<PageEntity, PageAdapter.VH>(Diff) {
    object Diff : DiffUtil.ItemCallback<PageEntity>() {
        override fun areItemsTheSame(oldItem: PageEntity, newItem: PageEntity) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: PageEntity, newItem: PageEntity) = oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemPageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding, onClick)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    class VH(
        private val binding: ItemPageBinding,
        private val onClick: (pageId: String) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PageEntity) {
            binding.icon.text = item.iconEmoji.ifBlank { "📝" }
            binding.title.text = item.title
            binding.updated.text = android.text.format.DateUtils.getRelativeTimeSpanString(item.updatedAt)
            binding.root.setOnClickListener { onClick(item.id) }
        }
    }
}

