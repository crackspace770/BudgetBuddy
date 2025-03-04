package com.android.budgetbuddy.ui.report

import androidx.lifecycle.ViewModel
import com.android.budgetbuddy.data.BudgetRepository

class ReportViewModel(private val repository: BudgetRepository):ViewModel() {

    fun saveLoginState(isLoggedIn1: String, isLoggedIn: Boolean) {
        repository.saveBoolean("isLoggedIn", isLoggedIn)
    }

}