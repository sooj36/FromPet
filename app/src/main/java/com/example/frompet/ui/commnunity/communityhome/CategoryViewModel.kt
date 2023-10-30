package com.example.frompet.ui.commnunity.communityhome

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.frompet.data.model.CommunityHomeData
import com.example.frompet.data.repository.category.CategoryRepository

class CategoryViewModel(
    private val categoryRepository: CategoryRepository
):ViewModel() {
    //버튼 카테고리
    private val _btnList: MutableLiveData<List<CommunityHomeData>> = MutableLiveData()
    val btnList: LiveData<List<CommunityHomeData>> get() = _btnList

    fun clickItemCategory(){

    }

}
