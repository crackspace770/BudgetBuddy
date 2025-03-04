package com.android.budgetbuddy.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(

    val email:String? = null,
    val fotoProfil:String? = null,

): Parcelable