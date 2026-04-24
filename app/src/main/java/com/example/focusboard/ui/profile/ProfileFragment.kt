package com.example.focusboard.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.focusboard.R
import com.example.focusboard.data.remote.dto.UserDto
import com.example.focusboard.databinding.FragmentProfileBinding
import com.example.focusboard.ui.common.UiState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.userState.observe(viewLifecycleOwner) { state ->
            binding.progress.isVisible = state is UiState.Loading
            binding.contentGroup.isVisible = state is UiState.Success
            binding.errorGroup.isVisible = state is UiState.Error

            when (state) {
                is UiState.Success -> bindUser(state.data)
                is UiState.Error -> binding.errorText.text = state.message
                is UiState.Loading -> { /* spinner visible */ }
            }
        }

        viewModel.logoutDone.observe(viewLifecycleOwner) { done ->
            if (done) {
                findNavController().navigate(R.id.action_profile_to_login)
            }
        }

        binding.retryButton.setOnClickListener { viewModel.loadUser() }
        binding.logoutButton.setOnClickListener { viewModel.logout() }
    }

    private fun bindUser(user: UserDto) {
        binding.userName.text = user.name
        binding.userEmail.text = user.email
        // Avatar letter
        binding.avatarText.text = user.name.firstOrNull()?.uppercase() ?: "?"
    }
}
