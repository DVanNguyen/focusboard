package com.example.focusboard.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.focusboard.R
import com.example.focusboard.data.local.datastore.AuthTokenStore
import com.example.focusboard.data.remote.api.FocusBoardApiService
import com.example.focusboard.data.remote.dto.LoginRequest
import com.example.focusboard.databinding.FragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment() {
    @Inject lateinit var api: FocusBoardApiService
    @Inject lateinit var authTokenStore: AuthTokenStore

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.registerLink.setOnClickListener {
            findNavController().navigate(R.id.action_login_to_register)
        }

        binding.loginButton.setOnClickListener {
            val email = binding.emailInput.text?.toString()?.trim().orEmpty()
            val password = binding.passwordInput.text?.toString().orEmpty()
            doLogin(email, password)
        }
    }

    private fun doLogin(email: String, password: String) {
        binding.errorText.isVisible = false
        binding.progress.isVisible = true

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val res = api.login(LoginRequest(email = email, password = password))
                val body = res.body()
                val token = body?.token
                if (res.isSuccessful && !token.isNullOrBlank()) {
                    authTokenStore.setToken(token)
                    findNavController().navigate(R.id.action_login_to_workspaceList)
                } else {
                    binding.errorText.isVisible = true
                    binding.errorText.text = getString(R.string.error_login_failed)
                }
            } catch (_: Exception) {
                binding.errorText.isVisible = true
                binding.errorText.text = getString(R.string.error_network)
            } finally {
                binding.progress.isVisible = false
            }
        }
    }
}

