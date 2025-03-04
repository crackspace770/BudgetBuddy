package com.android.budgetbuddy.data.model

import com.android.budgetbuddy.R


object AddCategoryOption {

    fun makananCategory(): List<CategoryItem> {
        return listOf(
            CategoryItem(1, "", R.drawable.add_fish, categoryType = 1),
            CategoryItem(2, "", R.drawable.add_fries, categoryType = 1),
            CategoryItem(3, "", R.drawable.add_pizza, categoryType = 1),
            CategoryItem(4, "", R.drawable.add_cake, categoryType = 1),
            CategoryItem(5, "", R.drawable.add_buah, categoryType = 1),
            CategoryItem(6, "", R.drawable.add_chili, categoryType = 1),
            CategoryItem(7, "", R.drawable.add_sayur, categoryType = 1),
            CategoryItem(8, "", R.drawable.add_daging, categoryType = 1),
            CategoryItem(9, "", R.drawable.add_eskrim, categoryType = 1),
            CategoryItem(10, "", R.drawable.add_roti, categoryType = 1)
        )
    }

    fun minumanCategory(): List<CategoryItem> {
        return listOf(
            CategoryItem(11, "", R.drawable.add_wine, categoryType = 1),
            CategoryItem(12, "", R.drawable.add_coconut, categoryType = 1),
            CategoryItem(13, "", R.drawable.add_coffee, categoryType = 1),
            CategoryItem(14, "", R.drawable.add_milk, categoryType = 1),
            CategoryItem(15, "", R.drawable.add_milkshake, categoryType = 1)
        )
    }

    fun pakaianCategory(): List<CategoryItem> {
        return listOf(
            CategoryItem(16, "", R.drawable.add_shirt, categoryType = 1),
            CategoryItem(17, "", R.drawable.add_skirt, categoryType = 1),
            CategoryItem(18, "", R.drawable.add_hoodie, categoryType = 1),
            CategoryItem(19, "", R.drawable.add_dress, categoryType = 1),
            CategoryItem(20, "", R.drawable.add_boots, categoryType = 1),
            CategoryItem(21, "", R.drawable.add_dalaman, categoryType = 1)
        )
    }

    fun rumahCategory(): List<CategoryItem> {
        return listOf(
            CategoryItem(22, "", R.drawable.add_chair, categoryType = 1),
            CategoryItem(23, "", R.drawable.add_refigerator, categoryType = 1),
            CategoryItem(24, "", R.drawable.add_air_conditioner, categoryType = 1),
            CategoryItem(25, "", R.drawable.add_bingkai, categoryType = 1),
            CategoryItem(26, "", R.drawable.add_humidifier, categoryType = 1),
            CategoryItem(27, "", R.drawable.add_lampu, categoryType = 1)
        )
    }

    fun kendaraanCategory(): List<CategoryItem> {
        return listOf(
            CategoryItem(28, "", R.drawable.add_mobil, categoryType = 1),
            CategoryItem(29, "", R.drawable.add_bus, categoryType = 1),
            CategoryItem(30, "", R.drawable.add_pesawat, categoryType = 1),
            CategoryItem(31, "", R.drawable.add_kereta, categoryType = 1),
            CategoryItem(32, "", R.drawable.add_kapal, categoryType = 1),
            CategoryItem(33, "", R.drawable.add_motor, categoryType = 1)
        )
    }

    fun keuanganCategory(): List<CategoryItem> {
        return listOf(
            CategoryItem(34, "", R.drawable.add_gaji, categoryType = 2),
            CategoryItem(35, "", R.drawable.add_nyogok, categoryType = 2),
            CategoryItem(36, "", R.drawable.add_lomba, categoryType = 2)
        )
    }

    fun lainyaCategory(): List<CategoryItem> {
        return listOf(
            CategoryItem(37, "", R.drawable.add_sekolah, categoryType = 1),
            CategoryItem(38, "", R.drawable.add_listrik, categoryType = 1),
            CategoryItem(39, "", R.drawable.ex_electric, categoryType = 1),
            CategoryItem(40, "", R.drawable.add_tanaman, categoryType = 1),
            CategoryItem(41, "", R.drawable.add_bensin, categoryType = 1),
            CategoryItem(42, "", R.drawable.add_kesehatan, categoryType = 1),
            CategoryItem(43, "", R.drawable.add_travel, categoryType = 1),
            CategoryItem(44, "", R.drawable.add_kecantikan, categoryType = 1)
        )
    }
}
