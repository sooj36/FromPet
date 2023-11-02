package com.example.frompet.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.frompet.data.model.User
import com.google.firebase.firestore.FirebaseFirestore

class HomeFilterViewModel: ViewModel() {
    private val firestore :FirebaseFirestore = FirebaseFirestore.getInstance()
    private val _petType = MutableLiveData<String?>()
    val petType: LiveData<String?> get() = _petType

    private val _gender = MutableLiveData<String?>()
    val gender: LiveData<String?> get() = _gender

    private val _filterList = MutableLiveData<List<User>>()
    val filterList: LiveData<List<User>> get() = _filterList


}





class HomeFilterViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeFilterViewModel::class.java)) {
            return HomeFilterViewModel() as T
        }
        throw IllegalArgumentException("Unknown")
    }
}
