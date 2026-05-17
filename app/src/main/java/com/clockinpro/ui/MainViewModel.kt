package com.clockinpro.ui

import androidx.lifecycle.ViewModel
import com.clockinpro.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    userRepository: UserRepository
) : ViewModel() {
    val isLoggedIn: Flow<Boolean> = userRepository.isLoggedIn()
}
