package com.android.budgetbuddy.data.di

import android.content.Context
import com.android.budgetbuddy.data.BudgetRepository
import com.android.budgetbuddy.data.dao.CategoryDatabase
import com.android.budgetbuddy.data.preference.UserPreference
import com.android.budgetbuddy.data.preference.dataStore


object Injection {

    fun provideRepository(context: Context): BudgetRepository {
        val pref = UserPreference.getInstance(context, context.dataStore,)
        val categoryDao = CategoryDatabase.getDatabase(context).categoryDao()


        return BudgetRepository( pref, categoryDao)

    }

}