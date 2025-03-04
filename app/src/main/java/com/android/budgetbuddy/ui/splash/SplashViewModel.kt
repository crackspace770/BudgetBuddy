package com.android.budgetbuddy.ui.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.android.budgetbuddy.data.BudgetRepository
import com.android.budgetbuddy.data.model.UserModel

class SplashViewModel(private val repository: BudgetRepository):ViewModel() {


    // Check if user is already logged in
    fun isUserLoggedIn(): Boolean {
        return repository.getPreferenceBoolean("isLoggedIn")
    }

    // Save login state
    fun saveLoginState(isLoggedIn: Boolean) {
        repository.saveBoolean("isLoggedIn", isLoggedIn)
    }

}