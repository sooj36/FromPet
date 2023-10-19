package com.example.frompet.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.frompet.data.model.UserModel
import java.lang.IllegalArgumentException

class MatchedUserViewModel : ViewModel() {
    private val _matchedUsers = MutableLiveData<List<UserModel>>()
    val matchedUsers: LiveData<List<UserModel>> = _matchedUsers

    fun updateMatchedUsers(users: List<UserModel>){
        _matchedUsers.value = users
    }
}



class MatchedViewModelFactory()
    :ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(MatchedUserViewModel::class.java)){
            return MatchedUserViewModel(

            ) as T
        }else{
            throw IllegalArgumentException("Not found ViewModel class")
        }
    }
    }
