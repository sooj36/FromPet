package com.example.frompet.ui.commnunity.communitydetail

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import com.example.frompet.data.model.CommunityData
import com.example.frompet.databinding.ActivityCommunityDetailBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CommunityDetailActivity : AppCompatActivity() {

    private var _binding : ActivityCommunityDetailBinding? = null
    private val binding get() = _binding!!

    private val auth = FirebaseAuth.getInstance()

    private val Detaildb = FirebaseFirestore.getInstance()

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCommunityDetailBinding.inflate(layoutInflater)

        setContentView(binding.root)

        // Intent에서 데이터 가져옴
        var communityData : CommunityData?

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            communityData = intent.getParcelableExtra("commuityData",CommunityData::class.java)
        } else {
            communityData = intent.getParcelableExtra("communityData", CommunityData::class.java)
        }


        // 화면에 표시
        val title = binding.tvDetailTitle
        val contents = binding.tvDetailContents

        // CommunityData에서 가져오기
        title.text = communityData?.title
        contents.text = communityData?.contents



        binding.backBtn.setOnClickListener {
            finish()
        }
    }
}