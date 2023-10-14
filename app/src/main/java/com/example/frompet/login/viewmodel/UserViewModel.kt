package com.example.frompet.login.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.frompet.login.data.UserModel
import com.example.frompet.login.repository.UserRepository
import com.example.frompet.login.repository.UserRepositoryImp

class UserViewModel( private val userRepository: UserRepository
):ViewModel() {
/*
    private val _userData = MutableLiveData<UserModel?>()
    val userData : LiveData<UserModel?> get() = _userData


    fun saveUser(userId: String) {
        userRepository.getUser(userId) { user ->
            _userData.value = user
        }
    }

    fun getUserData(): LiveData<UserModel?> {
        return _userData
    }*/
}


class UserViewModelFactory():ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(UserViewModel::class.java)){
            return UserViewModel(
                UserRepositoryImp()
            )as T
        }else{
            throw IllegalArgumentException("Not found ViewModel class.")
        }
    }
}