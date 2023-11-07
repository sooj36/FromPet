package com.example.frompet.ui.commnunity.community

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView
import com.example.frompet.R
import com.example.frompet.data.model.CommunityData
import com.example.frompet.databinding.ActivityCommunityBinding
import com.example.frompet.ui.commnunity.communityadd.CommunityAddActivity
import com.example.frompet.ui.commnunity.communitydetail.CommunityDetailActivity
import com.example.frompet.ui.commnunity.communityhome.CategoryClick
import com.google.firebase.auth.FirebaseAuth

class CommunityActivity : AppCompatActivity() {

    companion object {
        const val COMMUNITY_DATA = "communityData"
        const val EXTRA_DATA = "extra_data"
        const val EXTRA_PET_TYPE = "petT"
    }

    private var _binding: ActivityCommunityBinding? = null
    private val binding get() = _binding!!
    private val auth = FirebaseAuth.getInstance()
    private val viewModel : CommunityViewModel by viewModels()
    private var originalList = mutableListOf<CommunityData>()
    private val communityAdapter: CommunityAdapter by lazy {
        CommunityAdapter(
            ListClick = { item ->
                // 기존의 startActivity(intent) 대신 onCommunityItemClick을 호출합니다.
                onCommunityItemClick(item)
            }
        )
    }

    private val startForDetailResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val docsId = result.data?.getStringExtra(CommunityDetailActivity.DOCS_ID)
            docsId?.let { id ->
                viewModel.deleteCommunityData(id)
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCommunityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerview.adapter = communityAdapter
        binding.listSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                val searchTerms = query?.trim()?.split("\\s+".toRegex()) // 띄어쓰기로 단어 분리

                val filteredList = if (!searchTerms.isNullOrEmpty()) {
                    originalList.filter { item ->
                        // 검색어 중 하나라도 포함되는 경우 필터링
                        searchTerms.any { term ->
                            item.title.contains(term, ignoreCase = true)
                            item.contents.contains(term, ignoreCase = true)
                        }
                    }
                } else {
                    originalList // query가 빈 문자열이거나 null인 경우, 원본 목록을 반환
                }
                communityAdapter.submitList(filteredList)
                return true // 검색 이벤트 처리 완료
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrBlank()) {
                    return true
                }
                Log.e("sshOriginList Tag","$newText")
                return true
            }
        })


        val petType = intent.getStringExtra(EXTRA_PET_TYPE)
        if (petType != null) {
            fetchCommunityData(petType)
        }

        viewModel.filteredCommunityList.observe(this) { communityDataList ->
            if (communityDataList != null) {
                communityAdapter.submitList(communityDataList)
            }
        }


       /* binding.recyclerview.scrollToPosition(0) */// 수정 예정


        /*binding.ivCategory.setImageResource(petType)*/ //카테고리별 로고인데 int 값이라 안뜸
        binding.tvPetT.text = "$petType 카테고리"


        binding.backBtn.setOnClickListener {
            finish()
        }

        binding.ivPen.setOnClickListener {
            val intent: Intent =
                Intent(this@CommunityActivity, CommunityAddActivity::class.java)
            startActivity(intent)
        }

        binding.chipGroup.setOnCheckedChangeListener { group, checkedId ->
            val filter = getFilter()
            if (petType != null) {
                viewModel.getFilterCommunityData(petType, filter).observe(this) { filteredList ->
                    communityAdapter.submitList(filteredList)
                }
            }
        }

        viewModel.event.observe(this) { categoryClick ->
            when (categoryClick) {
                is CategoryClick.PetCategory -> {
                    val item = categoryClick.item
                    // 클릭된 데이터(item)를 사용하여 화면을 업데이트하거나 필요한 작업을 수행합니다.
                    val intent = Intent(this, CommunityDetailActivity::class.java)
                    intent.putExtra(COMMUNITY_DATA, item)
                    startActivity(intent)
                }
            }
        }
    }


    private fun fetchCommunityData(petType: String) {
        viewModel.getCommunityData(petType).observe(this) { communityDataList ->
            originalList.addAll(communityDataList)
            Log.e("sshOriginList be","$originalList")
            communityAdapter.submitList(communityDataList)
            Log.e("sshOriginList after","$originalList")
        }
    }


    private fun getFilter() =  when (binding.chipGroup.checkedChipId) {
        R.id.chip_share -> "나눔"
        R.id.chip_walk -> "산책"
        R.id.chip_love -> "사랑"
        R.id.chip_exchange -> "정보교환"
        R.id.chip_all -> "전체"
        else -> ""
    }
    private fun onCommunityItemClick(communityData: CommunityData) {
        val intent = Intent(this, CommunityDetailActivity::class.java).apply {
            putExtra(CommunityDetailActivity.COMMUNITY_DATA, communityData)
        }
        startForDetailResult.launch(intent)
    }
    override fun onBackPressed() {
        if (!binding.listSearch.isIconified) {
            binding.listSearch.isIconified = true
        } else {
            super.onBackPressed()
        }
    }
}

