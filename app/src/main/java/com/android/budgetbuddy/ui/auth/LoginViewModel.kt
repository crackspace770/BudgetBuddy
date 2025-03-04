package com.android.budgetbuddy.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.android.budgetbuddy.data.BudgetRepository
import com.android.budgetbuddy.data.model.UserModel
import com.android.budgetbuddy.data.preference.UserPreference
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: BudgetRepository) : ViewModel() {

    // Observe user data
    fun getUser(): LiveData<UserModel> {
        return repository.getUser().asLiveData()
    }

    // Check if user is already logged in
    fun isUserLoggedIn(): Boolean {
        return repository.getPreferenceBoolean("isLoggedIn")
    }

    // Save login state
    fun saveLoginState(isLoggedIn: Boolean) {
        repository.saveBoolean("isLoggedIn", isLoggedIn)
    }

    // Perform login and update state
    fun login(email: String, password: String, onResult: (Boolean) -> Unit) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    saveLoginState(true) // Save login status in repository
                    onResult(true)
                } else {
                    onResult(false)
                }
            }
    }
}

