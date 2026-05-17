package com.clockinpro.ui.checkin

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clockinpro.data.repository.CheckRecordRepository
import com.clockinpro.data.repository.UserRepository
import com.clockinpro.domain.model.CheckRecord
import com.clockinpro.domain.model.CheckType
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Locale
import javax.inject.Inject

data class CheckInUiState(
    val hasCheckedIn: Boolean = false,
    val hasCheckedOut: Boolean = false,
    val currentLocation: Location? = null,
    val currentAddress: String? = null,
    val photoPath: String? = null,
    val remark: String = "",
    val isLoading: Boolean = false,
    val isGettingLocation: Boolean = false,
    val successMessage: String? = null,
    val error: String? = null
)

@HiltViewModel
class CheckInViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val checkRecordRepository: CheckRecordRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(CheckInUiState())
    val uiState: StateFlow<CheckInUiState> = _uiState.asStateFlow()

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    init {
        loadTodayStatus()
    }

    private fun loadTodayStatus() {
        viewModelScope.launch {
            userRepository.getCurrentUserId().collect { userId ->
                if (userId != null) {
                    checkRecordRepository.getTodayRecords(userId).collect { records ->
                        val hasCheckedIn = records.any { it.type == CheckType.CHECK_IN }
                        val hasCheckedOut = records.any { it.type == CheckType.CHECK_OUT }

                        _uiState.value = _uiState.value.copy(
                            hasCheckedIn = hasCheckedIn,
                            hasCheckedOut = hasCheckedOut
                        )
                    }
                }
            }
        }
    }

    fun getCurrentLocation() {
        viewModelScope.launch {
            if (!hasLocationPermission()) {
                _uiState.value = _uiState.value.copy(
                    error = "需要定位权限"
                )
                return@launch
            }

            _uiState.value = _uiState.value.copy(isGettingLocation = true)

            try {
                val location = fusedLocationClient.lastLocation.await()
                if (location != null) {
                    val address = getAddressFromLocation(location)
                    _uiState.value = _uiState.value.copy(
                        currentLocation = location,
                        currentAddress = address,
                        isGettingLocation = false,
                        error = null
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isGettingLocation = false,
                        error = "无法获取位置，请确保GPS已开启"
                    )
                }
            } catch (e: SecurityException) {
                _uiState.value = _uiState.value.copy(
                    isGettingLocation = false,
                    error = "定位权限被拒绝"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isGettingLocation = false,
                    error = "获取位置失败"
                )
            }
        }
    }

    private suspend fun getAddressFromLocation(location: Location): String? {
        return withContext(Dispatchers.IO) {
            try {
                val geocoder = Geocoder(context, Locale.getDefault())
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    var result: String? = null
                    geocoder.getFromLocation(location.latitude, location.longitude, 1) { addresses ->
                        result = addresses.firstOrNull()?.let { address ->
                            buildString {
                                address.thoroughfare?.let { append(it) }
                                address.subThoroughfare?.let {
                                    if (isNotEmpty()) append(" ")
                                    append(it)
                                }
                                address.locality?.let {
                                    if (isNotEmpty()) append(", ")
                                    append(it)
                                }
                            }
                        }
                    }
                    result
                } else {
                    @Suppress("DEPRECATION")
                    val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                    addresses?.firstOrNull()?.let { address ->
                        buildString {
                            address.thoroughfare?.let { append(it) }
                            address.subThoroughfare?.let {
                                if (isNotEmpty()) append(" ")
                                append(it)
                            }
                            address.locality?.let {
                                if (isNotEmpty()) append(", ")
                                append(it)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                null
            }
        }
    }

    fun setPhotoPath(path: String?) {
        _uiState.value = _uiState.value.copy(photoPath = path)
    }

    fun updateRemark(remark: String) {
        _uiState.value = _uiState.value.copy(remark = remark)
    }

    fun checkIn() {
        performCheck(CheckType.CHECK_IN)
    }

    fun checkOut() {
        performCheck(CheckType.CHECK_OUT)
    }

    private fun performCheck(type: CheckType) {
        val state = _uiState.value

        if (state.currentLocation == null) {
            _uiState.value = state.copy(error = "请先获取位置")
            return
        }

        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true)

            try {
                userRepository.getCurrentUserId().first()?.let { userId ->
                    val record = CheckRecord(
                        userId = userId,
                        type = type,
                        latitude = state.currentLocation.latitude,
                        longitude = state.currentLocation.longitude,
                        address = state.currentAddress,
                        photoPath = state.photoPath,
                        remark = state.remark.ifBlank { null }
                    )

                    checkRecordRepository.insertRecord(record)

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        successMessage = if (type == CheckType.CHECK_IN) "签到成功" else "签退成功",
                        error = null,
                        hasCheckedIn = if (type == CheckType.CHECK_IN) true else _uiState.value.hasCheckedIn,
                        hasCheckedOut = if (type == CheckType.CHECK_OUT) true else _uiState.value.hasCheckedOut,
                        photoPath = null,
                        remark = ""
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "打卡失败"
                )
            }
        }
    }

    fun clearMessage() {
        _uiState.value = _uiState.value.copy(successMessage = null, error = null)
    }

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }
}
