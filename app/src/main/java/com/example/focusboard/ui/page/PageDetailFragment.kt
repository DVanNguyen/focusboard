package com.example.focusboard.ui.page

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.focusboard.data.local.db.entities.BlockType
import com.example.focusboard.databinding.FragmentPageDetailBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PageDetailFragment : Fragment() {
    private var _binding: FragmentPageDetailBinding? = null
    private val binding get() = _binding!!

    private val args: PageDetailFragmentArgs by navArgs()
    private val viewModel: PageDetailViewModel by viewModels()
    private val adapter = BlockAdapter(
        onContentChanged = { blockId, newContent -> viewModel.updateContent(blockId, newContent) },
        onTodoCheckedChanged = { blockId, checked -> viewModel.updateChecked(blockId, checked) },
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPageDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        viewModel.bind(args.pageId)

        viewModel.blocks.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
        }

        binding.fabText.setOnClickListener { viewModel.addBlock(BlockType.TEXT) }
        binding.fabTodo.setOnClickListener { viewModel.addBlock(BlockType.TODO) }

        val callback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT,
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder,
            ): Boolean {
                adapter.moveItem(viewHolder.bindingAdapterPosition, target.bindingAdapterPosition)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val item = adapter.currentList.getOrNull(viewHolder.bindingAdapterPosition) ?: return
                viewModel.deleteBlock(item.id)
            }

            override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
                super.clearView(recyclerView, viewHolder)
                viewModel.saveNewOrder(adapter.currentList)
            }
        }
        ItemTouchHelper(callback).attachToRecyclerView(binding.recyclerView)
    }
}

