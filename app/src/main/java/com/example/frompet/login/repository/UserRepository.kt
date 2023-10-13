package com.example.frompet.login.repository

import com.example.frompet.login.data.UserModel

interface UserRepository {
    suspend fun UserInfo(user:UserModel):List<UserModel>?
}