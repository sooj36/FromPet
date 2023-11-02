package com.example.frompet.ui.commnunity.community

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.frompet.data.model.CommunityData
import com.example.frompet.databinding.ActivityCommunityBinding
import com.example.frompet.ui.commnunity.communityadd.CommunityAddActivity
import com.example.frompet.ui.commnunity.communitydetail.CommunityDetailActivity
import com.example.frompet.ui.commnunity.communitydetail.CommunityDetailUpdateActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class CommunityActivity : AppCompatActivity() {

    companion object {
        const val COMMUNITY_DATA = "communityData"
    }

    private var _binding: ActivityCommunityBinding? = null
    private val binding get() = _binding!!
    private val auth = FirebaseAuth.getInstance()
    private val communityAdapter : CommunityAdapter by lazy { CommunityAdapter(
        ListClick = {item ->
            //전달하는 데이터
            val intent: Intent = Intent(this, CommunityDetailActivity::class.java)
            intent.putExtra(COMMUNITY_DATA, item)
            startActivity(intent)

        }
    ) }

    // viewModel 초기화
    private val viewModel : CommunityViewModel by viewModels()
    // FirebaseStorage 초기화
    val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCommunityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerview.adapter = communityAdapter
        binding.recyclerview.scrollToPosition(0) // 수정 예정

        // Firebase 현재 사용자 가져오기 (일단 남겨놈)
        val currentUser = FirebaseAuth.getInstance().currentUser

        viewModel.communityList.observe(this, { communityList ->
            communityAdapter.submitList(communityList)
        })


        binding.backBtn.setOnClickListener {
            finish()
        }

        binding.ivPen.setOnClickListener {
            val intent: Intent =
                Intent(this@CommunityActivity, CommunityAddActivity::class.java)
            startActivity(intent)
        }

        val chipLove = binding.chipLove
        binding.chipGroup.setOnClickListener {
            chipLove.chipBackgroundColor
        }
    }

    override fun onResume() {
        super.onResume()

        viewModel.loadCommunityListData()
    }
}