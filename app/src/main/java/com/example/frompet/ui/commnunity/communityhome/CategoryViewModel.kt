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
):ViewModel() {
    //버튼 카테고리
    private val _btnList: MutableLiveData<List<CommunityData>> = MutableLiveData()
    val btnList: LiveData<List<CommunityData>> get() = _btnList

    private val _commuHomeDataList: MutableLiveData<List<CommunityHomeData>> = MutableLiveData()
    val commuHomeDataList: LiveData<List<CommunityHomeData>> =_commuHomeDataList

    private val _communityList: MutableLiveData<List<CommunityData>> = MutableLiveData()
    val communityList: LiveData<List<CommunityData>> = _communityList

    private val _selectPetType: MutableLiveData<String> = MutableLiveData()
    val selectPetType:LiveData<String> = _selectPetType

    private val _event: SingleLiveEvent<CategoryClick> = SingleLiveEvent()
    val event: LiveData<CategoryClick> get() = _event


    private val _clickedCategoryData: MutableLiveData<CommunityData> = MutableLiveData()
    val clickedCategoryData: LiveData<CommunityData> get() = _clickedCategoryData

    fun onCategoryClicked(data: CommunityData) {
        _clickedCategoryData.value = data
    }

    fun getHomeCategory(){
        viewModelScope.launch {
            try{
                val categories = categoryRepository.getCategory()
                Log.e("zzzzzzz", "getHomeCategory executed successfully") // 디버그 로그
                _commuHomeDataList.postValue(categories)
            }catch (e:Exception){
                Log.e("zzzzz", "Error in getHomeCategory: ${e.message}", e) // 오류 로그
            }
        }
    }

    fun listClickCategory(petType: String) {
        viewModelScope.launch {
            try {
                Log.e("zzzzzzz", petType)
                val category = categoryRepository.getCommunityData(petType)
                val cateMatchCategory = category.filter { it.petType == petType }
                if (cateMatchCategory.isNotEmpty()) {
                    _communityList.value = cateMatchCategory
                    Log.e("zzzzzzz","$petType,$cateMatchCategory")
                } else {
                    Log.e("zzzzzzz", petType)
                }
            }catch (e: Exception){
                Log.e("zzzzzzz","errot",e)
            }
        }
    }
    fun onCategoryClicked(data: CommunityHomeData) {
        // 클릭한 카테고리 데이터를 Event로 전달
        _event.value = CategoryClick.PetCategory(data.toCommunityData())
    }

}
class CategoryViewModelFactory(
    private val context: Context
): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CategoryViewModel::class.java)) {
            Log.e("hhhhh", "CategoryViewModel instance created")
            return CategoryViewModel(CategoryRepositoryImp(context)) as T
        } else {
            throw IllegalArgumentException("Not found ViewModel class.")
        }
    }
}


