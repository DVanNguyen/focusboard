package com.example.focusboard.ui.tasks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.focusboard.R
import com.example.focusboard.databinding.FragmentTaskListBinding
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TaskListFragment : Fragment() {
    private var _binding: FragmentTaskListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TaskListViewModel by viewModels()
    private val adapter = TaskAdapter { blockId, isChecked ->
        viewModel.toggleTodo(blockId, isChecked)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTaskListBinding.inflate(inflater, container, false)
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

        // Filter chips
        binding.chipAll.setOnClickListener { viewModel.setFilter(TaskListViewModel.Filter.ALL) }
        binding.chipIncomplete.setOnClickListener { viewModel.setFilter(TaskListViewModel.Filter.INCOMPLETE) }
        binding.chipCompleted.setOnClickListener { viewModel.setFilter(TaskListViewModel.Filter.COMPLETED) }

        viewModel.filter.observe(viewLifecycleOwner) { filter ->
            binding.chipAll.isChecked = filter == TaskListViewModel.Filter.ALL
            binding.chipIncomplete.isChecked = filter == TaskListViewModel.Filter.INCOMPLETE
            binding.chipCompleted.isChecked = filter == TaskListViewModel.Filter.COMPLETED
        }

        viewModel.tasks.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
            binding.emptyGroup.isVisible = list.isEmpty()
            binding.recyclerView.isVisible = list.isNotEmpty()
        }
    }
}
