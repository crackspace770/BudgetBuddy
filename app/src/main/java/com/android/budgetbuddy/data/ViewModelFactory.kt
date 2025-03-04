package com.android.budgetbuddy.data

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.android.budgetbuddy.data.di.Injection
import com.android.budgetbuddy.data.preference.UserPreference
import com.android.budgetbuddy.ui.auth.LoginViewModel
import com.android.budgetbuddy.ui.category.CategoryViewModel
import com.android.budgetbuddy.ui.report.ReportViewModel
import com.android.budgetbuddy.ui.splash.SplashViewModel

class ViewModelFactory(private val repository: BudgetRepository) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(repository) as T
            }

            modelClass.isAssignableFrom(CategoryViewModel::class.java) -> {
                CategoryViewModel(repository) as T
            }

            modelClass.isAssignableFrom(ReportViewModel::class.java) -> {
                ReportViewModel(repository) as T
            }
            modelClass.isAssignableFrom(SplashViewModel::class.java) -> {
                SplashViewModel(repository) as T
            }


            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        private var instance: ViewModelFactory? = null

        fun getInstance(context: Context): ViewModelFactory {
            return instance ?: synchronized(this) {
                val repository = Injection.provideRepository(context)

                instance ?: ViewModelFactory(repository).also { instance = it }
            }
        }
    }

}