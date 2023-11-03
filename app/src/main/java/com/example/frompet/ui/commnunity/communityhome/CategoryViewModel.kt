package com.example.frompet.ui.commnunity.communityhome

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.frompet.data.model.CommunityData
import com.example.frompet.data.model.CommunityHomeData
import com.example.frompet.data.repository.category.CategoryRepository
import com.example.frompet.data.repository.category.CategoryRepositoryImp
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import io.grpc.internal.DnsNameResolver.SrvRecord
import kotlinx.coroutines.launch
import javax.inject.Inject
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

    fun getHomeCategory(){
        viewModelScope.launch {
            try{
                val categories = categoryRepository.getCategory()
                Log.e("zzzzz", "getHomeCategory executed successfully") // 디버그 로그
                _commuHomeDataList.value = categories
            }catch (e:Exception){
                Log.e("zzzzz", "Error in getHomeCategory: ${e.message}", e) // 오류 로그
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
                    Log.e("zzzzzz","$petType,$cateMatchCategory")
                } else {
                    Log.e("zzzzz","$petType")
                }
            }catch (e: Exception){
                Log.e("zzzzz","errot",e)
            }
        }
    }

    fun clickItemCategory(petType: String){
        _selectPetType.value = petType
    }

    fun CategoryAnimal(petType:String){
        viewModelScope.launch {
            try{
                val petTypeData = categoryRepository.getCommunityData(petType)
                _btnList.value = petTypeData
            }catch (e: Exception){
                Log.e("zzzzzz","Error ${e.message}",e)
            }
        }
    }

}
class CategoryViewModelFactory(
    private val context: Context
): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CategoryViewModel::class.java)) {
            return CategoryViewModel(CategoryRepositoryImp(context)) as T
        } else {
            throw IllegalArgumentException("Not found ViewModel class.")
        }
    }
}


