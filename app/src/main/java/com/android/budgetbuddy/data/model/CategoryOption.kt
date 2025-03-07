package com.android.budgetbuddy.data.model

import com.android.budgetbuddy.R

object CategoryOptions {

    fun expenseCategory(): List<CategoryItem> {
        return listOf(
            CategoryItem(1, "Makanan", R.drawable.ex_makanan, categoryType = 1),
            CategoryItem(2, "Transportasi", R.drawable.ex_kendaraan, categoryType = 1),
            CategoryItem(3, "Elektronik", R.drawable.add_elektronik, categoryType = 1),
            CategoryItem(4, "Pendidikan", R.drawable.ex_pendidikan, categoryType = 1),
            CategoryItem(5, "Tagihan Listrik", R.drawable.ex_electric, categoryType = 1),
            CategoryItem(6, "Shopping", R.drawable.ex_shopping, categoryType = 1),
            CategoryItem(7, "Tagihan Air", R.drawable.ex_water, categoryType = 1),
            CategoryItem(8, "Rumah", R.drawable.ex_rumah, categoryType = 1),
            CategoryItem(9, "Kesehatan", R.drawable.add_kesehatan, categoryType = 1)
        )
    }

    fun incomeCategory(): List<CategoryItem> {
        return listOf(
            CategoryItem(1, "Gaji", R.drawable.add_gaji, categoryType = 2),
            CategoryItem(2, "Menang Lomba", R.drawable.in_lomba, categoryType = 2),
            CategoryItem(3, "Bonus", R.drawable.in_bonus, categoryType = 2)
        )
    }
}
