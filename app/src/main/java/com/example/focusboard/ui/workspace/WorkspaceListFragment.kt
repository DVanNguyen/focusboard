package com.example.focusboard.ui.workspace

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.focusboard.databinding.FragmentWorkspaceListBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WorkspaceListFragment : Fragment() {
    private var _binding: FragmentWorkspaceListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: WorkspaceViewModel by viewModels()
    private val adapter = WorkspaceAdapter { wsId ->
        val action = WorkspaceListFragmentDirections.actionWorkspaceListToPageList(wsId)
        findNavController().navigate(action)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentWorkspaceListBinding.inflate(inflater, container, false)
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

        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            binding.progress.isVisible = loading
        }

        viewModel.workspaces.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
            val loading = viewModel.isLoading.value == true
            binding.emptyText.isVisible = list.isEmpty() && !loading
            binding.recyclerView.isVisible = list.isNotEmpty()
        }

        binding.fabAdd.setOnClickListener {
            viewModel.quickCreateWorkspace()
        }
    }
}
