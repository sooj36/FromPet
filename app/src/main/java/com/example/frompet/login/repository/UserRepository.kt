package com.example.frompet.login.repository

import com.example.frompet.login.data.UserModel

interface UserRepository {
    suspend fun saveUser(user:UserModel,callback:(Boolean)->Unit):List<UserModel>?

    fun getUser(userId: String, callback: (UserModel?) -> Unit)

}