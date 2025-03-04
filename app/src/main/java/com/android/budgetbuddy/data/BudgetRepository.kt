package com.android.budgetbuddy.data

import com.android.budgetbuddy.data.dao.CategoryDao
import com.android.budgetbuddy.data.model.CategoryItem
import com.android.budgetbuddy.data.model.UserModel
import com.android.budgetbuddy.data.model.UserToken
import com.android.budgetbuddy.data.preference.UserPreference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class BudgetRepository(
    private val preference: UserPreference,
    private val categoryDao: CategoryDao
) {

    // Get boolean preference
    fun getPreferenceBoolean(key: String): Boolean {
        return preference.getPreferenceBoolean(key)
    }

    // Save boolean preference
    fun saveBoolean(key: String, value: Boolean) {
        preference.saveBoolean(key, value)
    }

    /***----------------- CATEGORY DATABASE FUNCTIONS -----------------***/

    // Get categories by type (Expense or Income)
    fun getCategoriesByType(categoryType: Int): Flow<List<CategoryItem>> {
        return flow {
            emit(categoryDao.getCategoriesByType(categoryType))
        }.flowOn(Dispatchers.IO)
    }

    // Insert multiple categories (Avoids duplicates with OnConflictStrategy.IGNORE)
    suspend fun insertCategories(categories: List<CategoryItem>) {
        withContext(Dispatchers.IO) {
            categoryDao.insertAll(categories)
        }
    }

    // Clear all categories (Used when logging out)
    suspend fun clearCategories() {
        withContext(Dispatchers.IO) {
            categoryDao.deleteAllCategories()
        }
    }

    /***----------------- USER PREFERENCE FUNCTIONS -----------------***/

    // Retrieve User Data
    fun getUser(): Flow<UserModel> {
        return preference.getUser()
    }

    // Save user data
    suspend fun saveUserData(user: UserModel) {
        preference.saveUserData(user)
    }

    // Login user
    suspend fun login() {
        preference.login()
    }

    // Logout user & clear categories
    suspend fun logout() {
        preference.logout()
        clearCategories() // Ensure categories are deleted when user logs out
    }

    // Get User Token
    fun getUserToken(): Flow<UserToken> {
        return preference.getUserData()
    }

    // Save User Token
    suspend fun saveUserToken(token: UserToken) {
        preference.saveUserData(token)
    }

    // Delete User Token
    suspend fun deleteToken() {
        preference.deleteToken()
    }

    /***----------------- NOTIFICATION SETTINGS -----------------***/

    // Get Notification Setting (Default is true)
    fun getNotificationSetting(): Flow<Boolean> {
        return preference.getNotificationSetting()
    }

    // Save Notification Setting
    suspend fun saveNotificationSetting(isNotificationActive: Boolean) {
        preference.saveNotificationSetting(isNotificationActive)
    }


}