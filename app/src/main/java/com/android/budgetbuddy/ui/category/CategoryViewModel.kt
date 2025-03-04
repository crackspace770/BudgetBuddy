package com.android.budgetbuddy.ui.category

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.budgetbuddy.data.BudgetRepository
import com.android.budgetbuddy.data.dao.CategoryDao
import com.android.budgetbuddy.data.model.CategoryItem
import com.android.budgetbuddy.data.model.CategoryOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class CategoryViewModel(private val repository: BudgetRepository):ViewModel() {

    private val _categories = MutableLiveData<List<CategoryItem>>()
    val categories: LiveData<List<CategoryItem>> get() = _categories

    // Load categories by type and prepopulate defaults if needed
    fun loadCategories(categoryType: Int) {
        viewModelScope.launch {
            repository.getCategoriesByType(categoryType).collect { dbCategories ->
                if (dbCategories.isEmpty()) {
                    // Prepopulate categories if empty
                    val defaultCategories = if (categoryType == 1) {
                        CategoryOptions.expenseCategory().map { it.copy(categoryType = 1) }
                    } else {
                        CategoryOptions.incomeCategory().map { it.copy(categoryType = 2) }
                    }
                    repository.insertCategories(defaultCategories)
                }
                _categories.postValue(repository.getCategoriesByType(categoryType).first()) // Fetch updated categories
            }
        }
    }

    // Add a new category to the database
    fun addCategory(category: CategoryItem) {
        viewModelScope.launch {
            repository.insertCategories(listOf(category))
            loadCategories(category.categoryType) // Refresh the list
        }
    }


}