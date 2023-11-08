package com.example.frompet.ui.commnunity.communityhome

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.frompet.SingleLiveEvent
import com.example.frompet.data.model.CommunityData
import com.example.frompet.data.model.CommunityHomeData
import com.example.frompet.data.model.toCommunityData
import com.example.frompet.data.repository.category.CategoryRepository
import com.example.frompet.data.repository.category.CategoryRepositoryImp
import kotlinx.coroutines.launch
import kotlin.Exception


class CategoryViewModel(
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _commuHomeDataList: MutableLiveData<List<CommunityHomeData>> = MutableLiveData()
    val commuHomeDataList: LiveData<List<CommunityHomeData>> = _commuHomeDataList

    private val _communityList: MutableLiveData<List<CommunityData>> = MutableLiveData()
    val communityList: LiveData<List<CommunityData>> = _communityList

    private val _selectPetType: MutableLiveData<String> = MutableLiveData()
    val selectPetType: LiveData<String> = _selectPetType

    private val _event: SingleLiveEvent<CategoryClick> = SingleLiveEvent()
    val event: LiveData<CategoryClick> get() = _event

    private val _clickedCategoryData: MutableLiveData<CommunityData> = MutableLiveData()
    val clickedCategoryData: LiveData<CommunityData> get() = _clickedCategoryData

    fun onCategoryClicked(data: CommunityData) {
        _clickedCategoryData.value = data
        _event.value = CategoryClick.PetCategory(CommunityData())
    }

    fun getHomeCategory() {
        viewModelScope.launch {
            try {
                val categories = categoryRepository.getCategory()
                _commuHomeDataList.postValue(categories)
                val allCommunityData = mutableListOf<CommunityData>()
                for (category in categories) {
                    val petType = category.petType
                    val communityData = categoryRepository.getCommunityData(petType)
                    allCommunityData.addAll(communityData)
                }

                _communityList.postValue(allCommunityData)
            } catch (e: Exception) {
            }
        }
    }

    fun listClickCategory(petType: String) {
        viewModelScope.launch {
            try {
                val category = categoryRepository.getCommunityData(petType)
                val cateMatchCategory = category.filter { it.petType == petType }
                if (cateMatchCategory.isNotEmpty()) {
                    _communityList.value = cateMatchCategory
                    _selectPetType.value = petType
                } else {

                }
            } catch (e: Exception) {
            }

        }
    }

}

class CategoryViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CategoryViewModel::class.java)) {
            return CategoryViewModel(CategoryRepositoryImp(context)) as T
        } else {
            throw IllegalArgumentException("Not found ViewModel class.")
        }
    }
}


