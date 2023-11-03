package com.example.frompet.ui.commnunity.communityhome

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.frompet.data.model.CommunityHomeData
import com.example.frompet.data.repository.category.CategoryRepository
import com.example.frompet.data.repository.category.CategoryRepositoryImp
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject


class CategoryViewModel(
    private val categoryRepository: CategoryRepository
):ViewModel() {
    //버튼 카테고리
    private val _btnList: MutableLiveData<List<CommunityHomeData>> = MutableLiveData()
    val btnList: LiveData<List<CommunityHomeData>> get() = _btnList

    private val _commuHomeDataList: MutableLiveData<List<CommunityHomeData>> = MutableLiveData()
    val commuHomeDataList: LiveData<List<CommunityHomeData>> =_commuHomeDataList

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

    fun clickItemCategory(category: CommunityHomeData){
        _btnList.value = listOf(category)
    }

    fun CategoryAnimal(){
        viewModelScope.launch {
            val category = categoryRepository.getCategory()
            _btnList.value = category
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


