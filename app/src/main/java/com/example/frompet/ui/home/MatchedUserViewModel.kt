package com.example.frompet.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.frompet.data.model.User
import java.lang.IllegalArgumentException

class MatchedUserViewModel : ViewModel() {
    private val _matchedUsers = MutableLiveData<List<User>>()
    val matchedUsers: LiveData<List<User>> = _matchedUsers

    fun updateMatchedUsers(users: List<User>){
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
