package com.example.focusboard.ui.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.focusboard.R
import com.example.focusboard.data.local.datastore.AuthTokenStore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SplashFragment : Fragment() {
    @Inject lateinit var authTokenStore: AuthTokenStore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = inflater.inflate(R.layout.fragment_splash, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            val token = authTokenStore.token.first()
            val nav = findNavController()
            if (token.isNullOrBlank()) {
                nav.navigate(R.id.action_splash_to_login)
            } else {
                nav.navigate(R.id.action_splash_to_workspaceList)
            }
        }
    }
}

