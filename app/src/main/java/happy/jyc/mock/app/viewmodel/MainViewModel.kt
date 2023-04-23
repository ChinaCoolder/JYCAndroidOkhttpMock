package happy.jyc.mock.app.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import happy.jyc.mock.app.net.User
import happy.jyc.mock.app.net.UserWithHeader
import happy.jyc.mock.app.repo.UserRepository
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userRepository: UserRepository
): ViewModel() {
    private val _user = MutableStateFlow<User?>(null)
    val user: Flow<User?> = _user

    private val _userWait = MutableStateFlow<User?>(null)
    val userWait: Flow<User?> = _userWait

    private val _userByParam = MutableStateFlow<User?>(null)
    val userByParam: Flow<User?> = _userByParam

    private val _userByHeader = MutableStateFlow<User?>(null)
    val userByHeader: Flow<User?> = _userByHeader

    private val _disableUser = MutableStateFlow<User?>(null)
    val disableUser: Flow<User?> = _disableUser

    private val _userWithHeader = MutableStateFlow<UserWithHeader?>(null)
    val userWithHeader: Flow<UserWithHeader?> = _userWithHeader

    private val _error = MutableStateFlow<String?>(null)
    val error: Flow<String?> = _error

    fun fetchWaitUser() {
        userRepository.getUser(4)
            .onEach {
                _userWait.triggerValue(it)
            }
            .catch {
                Log.e("GetUserError", it.message.orEmpty())
                _error.triggerValue(it.message)
            }
            .launchIn(viewModelScope)
    }

    fun fetchUserByHeader() {
        userRepository.getUserByHeader("1")
            .onEach {
                _userByHeader.triggerValue(it)
            }
            .catch {
                Log.e("GetUserError", it.message.orEmpty())
                _error.triggerValue(it.message)
            }
            .launchIn(viewModelScope)
    }

    fun fetchUserByParam() {
        userRepository.getUserByParam("1")
            .onEach {
                _userByParam.triggerValue(it)
            }
            .catch {
                Log.e("GetUserError", it.message.orEmpty())
                _error.triggerValue(it.message)
            }
            .launchIn(viewModelScope)
    }

    fun fetchUserDisable() {
        userRepository.getUser(3)
            .onEach {
                _disableUser.triggerValue(it)
            }
            .catch {
                Log.e("GetUserError", it.message.orEmpty())
                _error.triggerValue(it.message)
            }
            .launchIn(viewModelScope)
    }

    fun fetchUser() {
        userRepository.getUser(1)
            .onEach {
                _user.triggerValue(it)
            }
            .catch {
                Log.e("GetUserError", it.message.orEmpty())
                _error.triggerValue(it.message)
            }
            .launchIn(viewModelScope)
    }

    fun fetchUserWithResponseHeader() {
        userRepository.getUserWithHeader(2)
            .onEach {
                _userWithHeader.triggerValue(it)
            }
            .catch {
                Log.e("GetUserError", it.message.orEmpty())
                _error.triggerValue(it.message)
            }
            .launchIn(viewModelScope)
    }

    private fun<T> MutableStateFlow<T?>.triggerValue(
        trigger: T
    ) {
        value = null
        value = trigger
    }
}