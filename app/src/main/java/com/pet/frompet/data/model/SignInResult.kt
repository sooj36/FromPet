package com.pet.frompet.data.model

import android.service.autofill.UserData

data class SignInResult(
    val data: UserData,
    val errorMessage: String?
)


