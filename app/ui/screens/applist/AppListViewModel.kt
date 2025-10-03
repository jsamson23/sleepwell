package com.example.morningfocusalarm.ui.screens.applist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.morningfocusalarm.data.repository.AlarmRepository
import com.example.morningfocusalarm.data.repository.AppRepository
import com.example.morningfocusalarm.model.AppInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppListViewModel @Inject constructor(
    private val appRepository: AppRepository,
    private val alarmRepository: AlarmRepository
) : ViewModel() {
    
    private val _apps = MutableStateFlow<List<AppInfo>>(emptyList())
    val apps: StateFlow<List<AppInfo>> = _apps.asStateFlow()
    
    private val _selectedApps = MutableStateFlow<Set<String>>(emptySet())
    val selectedApps: StateFlow<Set<String>> = _selectedApps.asStateFlow()
    
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    init {
        loadApps()
    }
    
    private fun loadApps() {
        viewModelScope.launch {
            _isLoading.value = true
            val settings = alarmRepository.settings.first()
            _selectedApps.value = settings.lockedAppPackages
            _apps.value = appRepository.getInstalledApps()
            _isLoading.value = false
        }
    }
    
    fun toggleApp(packageName: String) {
        val current = _selectedApps.value.toMutableSet()
        if (current.contains(packageName)) {
            current.remove(packageName)
        } else {
            current.add(packageName)
        }
        _selectedApps.value = current
    }
    
    fun saveSelection() {
        viewModelScope.launch {
            val settings = alarmRepository.settings.first()
            alarmRepository.updateSettings(
                settings.copy(lockedAppPackages = _selectedApps.value)
            )
        }
    }
}
