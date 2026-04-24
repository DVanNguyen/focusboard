package com.example.focusboard.ui.page

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.focusboard.R
import com.example.focusboard.databinding.FragmentPageListBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PageListFragment : Fragment() {
    private var _binding: FragmentPageListBinding? = null
    private val binding get() = _binding!!

    private val args: PageListFragmentArgs by navArgs()
    private val viewModel: PageListViewModel by viewModels()
    private val adapter = PageAdapter { pageId ->
        val action = PageListFragmentDirections.actionPageListToPageDetail(pageId)
        findNavController().navigate(action)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPageListBinding.inflate(inflater, container, false)
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

        viewModel.bind(args.workspaceId)

        viewModel.pages.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
            binding.emptyText.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
        }

        binding.fabAdd.setOnClickListener {
            viewModel.quickCreatePage()
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = true
            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.setQuery(newText.orEmpty())
                return true
            }
        })
    }
}

