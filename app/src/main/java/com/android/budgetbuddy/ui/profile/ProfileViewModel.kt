package com.android.budgetbuddy.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.android.budgetbuddy.data.BudgetRepository
import kotlinx.coroutines.launch

class ProfileViewModel(private val repository: BudgetRepository):ViewModel() {

    fun getNotificationSettings(): LiveData<Boolean> { // New method for notification
        return repository.getNotificationSetting().asLiveData()
    }


    fun saveNotificationSetting(isNotificationActive: Boolean) { // New method for notification
        viewModelScope.launch {
            repository.saveNotificationSetting(isNotificationActive)
        }
    }

    fun saveLoginState(isLoggedIn1: String, isLoggedIn: Boolean) {
        repository.saveBoolean("isLoggedIn", isLoggedIn)
    }
}