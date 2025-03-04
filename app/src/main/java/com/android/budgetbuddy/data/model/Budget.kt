package com.android.budgetbuddy.data.model

data class Budget(
    var title: String,
    var image: String, // URL or formatted drawable resource ID
    var limit: Int = 0,
    var pengeluaran: Int = 0,
    var sisa: Int = 0,
    var tanggal: String = ""
) {
    // Overloaded constructor to accept drawable resource IDs directly
    constructor(title: String, imageResId: Int, limit: Int = 0, pengeluaran: Int = 0, sisa: Int = 0, tanggal: String = "")
            : this(
        title = title,
        image = "res://drawable/$imageResId", // Custom format to distinguish resources
        limit = limit,
        pengeluaran = pengeluaran,
        sisa = sisa,
        tanggal = tanggal
    )
}
