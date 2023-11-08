package com.pet.frompet.ui.commnunity

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pet.frompet.data.model.CommunityData

class CategorySharedViewModel : ViewModel() {
    private val _selectCategory = MutableLiveData<CommunityData>()
    val selectCategory: LiveData<CommunityData> = _selectCategory


    fun selectPetCategory(data:CommunityData){
        _selectCategory.value = data
        Log.e("ssh4","${data}")
    }

}