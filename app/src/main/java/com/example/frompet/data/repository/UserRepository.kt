package com.example.frompet.data.repository

import com.example.frompet.data.model.User

interface UserRepository {
    suspend fun saveUser(user: User, callback:(Boolean)->Unit):List<User>?

    fun getUser(userId: String, callback: (User?) -> Unit)

}