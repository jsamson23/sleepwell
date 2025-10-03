package com.example.morningfocusalarm.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.morningfocusalarm.data.repository.AlarmRepository
import com.example.morningfocusalarm.util.PermissionHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val alarmRepository: AlarmRepository,
    private val permissionHelper: PermissionHelper
) : ViewModel() {
    
    val settings = alarmRepository.settings.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        com.example.morningfocusalarm.model.AlarmSettings()
    )
    
    val hasAllPermissions = kotlinx.coroutines.flow.flow {
        while (true) {
            emit(permissionHelper.hasAllPermissions())
            kotlinx.coroutines.delay(1000)
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        false
    )
    
    fun setEnabled(enabled: Boolean) {
        viewModelScope.launch {
            alarmRepository.setEnabled(enabled)
        }
    }
}