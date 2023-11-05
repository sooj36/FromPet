package com.example.frompet.ui.commnunity.community

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
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
    private val communityAdapter : CommunityAdapter by lazy { CommunityAdapter(
        ListClick = {item ->
            val updatedCommunityList = mutableListOf<CommunityData>().apply {
                addAll(communityAdapter.currentList) // Copy the current items
                add(item) // Add the clicked item
            }
            //전달하는 데이터
            val intent: Intent = Intent(this, CommunityDetailActivity::class.java)
            Log.d("sooj", "item ${item}")
            intent.putExtra(COMMUNITY_DATA, item)
            startActivity(intent)


        }
    ) }

    // viewModel 초기화
    private val viewModel : CommunityViewModel by viewModels()
    // FirebaseStorage 초기화


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCommunityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val communityData = intent.getParcelableExtra<CommunityData>(CommunityActivity.EXTRA_DATA)
        val petType = intent.getStringExtra(CommunityActivity.EXTRA_PET_TYPE)
        if (communityData != null) {
            // 데이터 사용
            Log.d("ㅂㅂㅂㅂㅂ", "item ${communityData}")
            /*communityAdapter.updateData(communityData)*/
        }
        if (petType != null) {
            // 필터 정보 사용
            viewModel.loadCommunityListData(petType)
            Log.d("ㅂㅂㅂㅂ", "petType $petType")
        }


        binding.recyclerview.adapter = communityAdapter
        binding.recyclerview.scrollToPosition(0) // 수정 예정

        // Firebase 현재 사용자 가져오기 (일단 남겨놈)
        val currentUser = FirebaseAuth.getInstance().currentUser

        viewModel.communityList.observe(this) { communityList ->
            communityAdapter.submitList(communityList)
        }


        binding.backBtn.setOnClickListener {
            finish()
        }

        binding.ivPen.setOnClickListener {
            val intent: Intent =
                Intent(this@CommunityActivity, CommunityAddActivity::class.java)
            startActivity(intent)
        }

        binding.chipGroup.setOnCheckedChangeListener { group, checkedId ->
            val currentFilter = getFilter()
            Log.d("sooj", "123 ${currentFilter}")
            viewModel.loadCommunityListData(currentFilter)

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

    override fun onResume() {
        super.onResume()

        viewModel.loadCommunityListData(getFilter())
    }
    private fun getFilter() =  when (binding.chipGroup.checkedChipId) {
        R.id.chip_share -> "나눔"
        R.id.chip_walk -> "산책"
        R.id.chip_love -> "사랑"
        R.id.chip_exchange -> "정보교환"
        R.id.chip_all -> "전체"
        else -> ""
    }
}