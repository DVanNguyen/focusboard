package com.example.focusboard.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.focusboard.data.local.datastore.AuthTokenStore
import com.example.focusboard.data.local.db.FocusBoardDatabase
import com.example.focusboard.data.remote.api.FocusBoardApiService
import com.example.focusboard.data.remote.dto.UserDto
import com.example.focusboard.ui.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val api: FocusBoardApiService,
    private val authTokenStore: AuthTokenStore,
    private val db: FocusBoardDatabase,
) : ViewModel() {

    private val _userState = MutableLiveData<UiState<UserDto>>(UiState.Loading)
    val userState: LiveData<UiState<UserDto>> = _userState

    private val _logoutDone = MutableLiveData(false)
    val logoutDone: LiveData<Boolean> = _logoutDone

    init {
        loadUser()
    }

    fun loadUser() {
        _userState.value = UiState.Loading
        viewModelScope.launch {
            try {
                val res = api.getMe()
                val user = res.body()?.data
                if (res.isSuccessful && user != null) {
                    _userState.value = UiState.Success(user)
                } else {
                    _userState.value = UiState.Error("Không thể tải thông tin người dùng")
                }
            } catch (_: Exception) {
                _userState.value = UiState.Error("Lỗi mạng. Vui lòng thử lại.")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                api.logout()
            } catch (_: Exception) {
                // Ignore — vẫn xóa token local
            }

            // Clear token
            authTokenStore.clear()

            // Clear Room database
            withContext(Dispatchers.IO) {
                db.clearAllTables()
            }

            _logoutDone.value = true
        }
    }
}
