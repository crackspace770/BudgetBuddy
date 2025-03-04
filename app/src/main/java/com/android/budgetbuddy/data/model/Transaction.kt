package com.android.budgetbuddy.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Transaction (
    var transactionID: String? =null,
    var type: Int? =null,
    var title: String? =null,
    var category: String? =null,
    var amount: Double? =null,
    var date: Long? =null,
    var note: String? =null,
    var invertedDate: Long?=null,
    var icon:String?=null

):Parcelable