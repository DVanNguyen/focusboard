package com.example.focusboard.ui.page

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.focusboard.data.local.db.entities.BlockEntity
import com.example.focusboard.data.local.db.entities.BlockType
import com.example.focusboard.databinding.ItemBlockDividerBinding
import com.example.focusboard.databinding.ItemBlockHeaderBinding
import com.example.focusboard.databinding.ItemBlockTextBinding
import com.example.focusboard.databinding.ItemBlockTodoBinding
import java.util.Collections

class BlockAdapter(
    private val onContentChanged: (blockId: String, newContent: String) -> Unit,
    private val onTodoCheckedChanged: (blockId: String, checked: Boolean) -> Unit,
) : ListAdapter<BlockEntity, RecyclerView.ViewHolder>(Diff) {

    object Diff : DiffUtil.ItemCallback<BlockEntity>() {
        override fun areItemsTheSame(oldItem: BlockEntity, newItem: BlockEntity) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: BlockEntity, newItem: BlockEntity) = oldItem == newItem
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position).type) {
            BlockType.H1, BlockType.H2 -> VT_HEADER
            BlockType.TODO -> VT_TODO
            BlockType.DIVIDER -> VT_DIVIDER
            else -> VT_TEXT
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VT_HEADER -> HeaderVH(ItemBlockHeaderBinding.inflate(inflater, parent, false), onContentChanged)
            VT_TODO -> TodoVH(ItemBlockTodoBinding.inflate(inflater, parent, false), onContentChanged, onTodoCheckedChanged)
            VT_DIVIDER -> DividerVH(ItemBlockDividerBinding.inflate(inflater, parent, false))
            else -> TextVH(ItemBlockTextBinding.inflate(inflater, parent, false), onContentChanged)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HeaderVH -> holder.bind(getItem(position))
            is TextVH -> holder.bind(getItem(position))
            is TodoVH -> holder.bind(getItem(position))
            is DividerVH -> holder.bind(getItem(position))
        }
    }

    fun moveItem(from: Int, to: Int) {
        if (from == to) return
        val mutable = currentList.toMutableList()
        Collections.swap(mutable, from, to)
        submitList(mutable)
    }

    class HeaderVH(
        private val binding: ItemBlockHeaderBinding,
        private val onContentChanged: (blockId: String, newContent: String) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {
        private var currentId: String? = null
        private val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val id = currentId ?: return
                onContentChanged(id, s?.toString().orEmpty())
            }
        }

        init {
            binding.editText.addTextChangedListener(watcher)
        }

        fun bind(item: BlockEntity) {
            currentId = item.id
            if (binding.editText.text?.toString() != item.content) {
                binding.editText.setText(item.content)
                binding.editText.setSelection(binding.editText.text?.length ?: 0)
            }
            binding.editText.textSize = if (item.type == BlockType.H1) 22f else 18f
        }
    }

    class TextVH(
        private val binding: ItemBlockTextBinding,
        private val onContentChanged: (blockId: String, newContent: String) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {
        private var currentId: String? = null
        private val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val id = currentId ?: return
                onContentChanged(id, s?.toString().orEmpty())
            }
        }

        init {
            binding.editText.addTextChangedListener(watcher)
        }

        fun bind(item: BlockEntity) {
            currentId = item.id
            if (binding.editText.text?.toString() != item.content) {
                binding.editText.setText(item.content)
                binding.editText.setSelection(binding.editText.text?.length ?: 0)
            }
        }
    }

    class TodoVH(
        private val binding: ItemBlockTodoBinding,
        private val onContentChanged: (blockId: String, newContent: String) -> Unit,
        private val onCheckedChanged: (blockId: String, checked: Boolean) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {
        private var currentId: String? = null
        private val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val id = currentId ?: return
                onContentChanged(id, s?.toString().orEmpty())
            }
        }

        init {
            binding.editText.addTextChangedListener(watcher)
            binding.checkbox.setOnCheckedChangeListener { _, isChecked ->
                val id = currentId ?: return@setOnCheckedChangeListener
                onCheckedChanged(id, isChecked)
            }
        }

        fun bind(item: BlockEntity) {
            currentId = item.id
            binding.checkbox.isChecked = item.isChecked
            if (binding.editText.text?.toString() != item.content) {
                binding.editText.setText(item.content)
                binding.editText.setSelection(binding.editText.text?.length ?: 0)
            }
        }
    }

    class DividerVH(
        private val binding: ItemBlockDividerBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: BlockEntity) = Unit
    }

    private companion object {
        const val VT_HEADER = 1
        const val VT_TEXT = 2
        const val VT_TODO = 3
        const val VT_DIVIDER = 4
    }
}

