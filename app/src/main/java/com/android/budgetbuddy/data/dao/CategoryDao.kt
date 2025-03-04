package com.android.budgetbuddy.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.android.budgetbuddy.data.model.CategoryItem


@Dao
interface CategoryDao {
    @Query("SELECT * FROM CategoryItem WHERE categoryType = :categoryType")
    suspend fun getCategoriesByType(categoryType: Int): List<CategoryItem>

    @Insert(onConflict = OnConflictStrategy.IGNORE) // Avoid duplicate entries
    suspend fun insertAll(categories: List<CategoryItem>)

    @Query("DELETE FROM CategoryItem") // Delete all categories
    suspend fun deleteAllCategories()

}