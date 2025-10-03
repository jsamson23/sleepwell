package com.example.morningfocusalarm

import androidx.lifecycle.ViewModel
import com.example.morningfocusalarm.data.local.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    preferencesManager: PreferencesManager
) : ViewModel() {
    val isOnboardingCompleted = preferencesManager.isOnboardingCompleted
}