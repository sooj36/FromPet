package com.pet.frompet.data.model

data class SignState(
    val isSignInSuccessful: Boolean = false,
    val signInError: String? = null,
    val user:User? = null
)
