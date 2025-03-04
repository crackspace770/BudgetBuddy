package com.android.budgetbuddy.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
data class CategoryItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val icon: Int,
    val categoryType: Int
) : Parcelable {
    override fun toString(): String {
        return name
    }
}

