package com.example.frompet.data.repository

import com.example.frompet.data.model.UserModel

interface UserRepository {
    suspend fun saveUser(user: UserModel, callback:(Boolean)->Unit):List<UserModel>?

    fun getUser(userId: String, callback: (UserModel?) -> Unit)

}