package com.example.frompet.ui.commnunity.communityhome

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.frompet.data.model.CommunityHomeData
import com.example.frompet.data.repository.category.CategoryRepository
import com.example.frompet.data.repository.category.CategoryRepositoryImp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


class CategoryViewModel(
    private val categoryRepository: CategoryRepository
):ViewModel() {
    //버튼 카테고리
    private val _btnList: MutableLiveData<List<CommunityHomeData>> = MutableLiveData()
    val btnList: LiveData<List<CommunityHomeData>> get() = _btnList

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
/*class CategoryViewModelFactory(

): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CategoryViewModel::class.java)) {
            return CategoryViewModel(
                CategoryRepositoryImp()
            ) as T
        } else {
            throw IllegalArgumentException("Not found ViewModel class.")
        }
    }
}*/

