package com.example.frompet.data.repository.matched

import com.example.frompet.data.model.User

interface MatchedRepository {

    suspend fun like(targetUserId: String)
    suspend fun disLike(targetUserId: String)
    suspend fun loadLike()
    suspend fun loadAlreadyActionUsers(load: (List<String>) -> Unit)
    suspend fun getExceptDislikeAndMe(onSuccess: (List<User>) -> Unit, onFailure: (Exception) -> Unit)
    suspend fun matchUser(otherUserUid: String)
    suspend fun loadMatchedUsers()
    suspend fun removeMatchedUser(targetUserId: String)
}