package com.example.frompet.ui.commnunity.communitydetail

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.frompet.data.model.CommunityData
import com.example.frompet.databinding.ActivityCommunityDetailUpdateBinding
import com.example.frompet.util.showToast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CommunityDetailUpdateActivity : AppCompatActivity() {

    private var _binding: ActivityCommunityDetailUpdateBinding? = null
    private val binding get() = _binding!!

    private val currentUser = FirebaseAuth.getInstance().currentUser
    private val store = FirebaseFirestore.getInstance()
    private var communityData: CommunityData? = null

    companion object {
        const val COMMUNITY_DATA = "communityData"
        const val DOCS_ID = "docsId"
    }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCommunityDetailUpdateBinding.inflate(layoutInflater)

        setContentView(binding.root)

        communityData = intent.getParcelableExtra(CommunityDetailActivity.DOCS_ID)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            communityData = intent.getParcelableExtra(CommunityDetailActivity.COMMUNITY_DATA, CommunityData::class.java)
        } else {
            communityData = intent.extras?.getParcelable(CommunityDetailActivity.COMMUNITY_DATA) as CommunityData?
        }

        // 화면에 표시
        val title = binding.updateTitle
        val contents = binding.updateContents

        // CommunityData에서 가져오기
        title.setText(communityData?.title)
        contents.setText(communityData?.contents)

        binding.backBtn.setOnClickListener {
            finish()
        }

        binding.btnDone.setOnClickListener {
            updateCommunity(communityData?.docsId)
        }
    }

    private fun updateCommunity(docsId: String?) {
        if (docsId != null) {
            store.collection("Community")
                .document(docsId)
                .update(
                    "title",
                    binding.updateTitle.text.toString(),
                    "contents",
                    binding.updateContents.text.toString()
                )
                .addOnSuccessListener {
                    showToast("게시글이 수정되었습니다", Toast.LENGTH_SHORT)
                    finish()
                }
                .addOnFailureListener {
                    showToast("게시글이 수정 권한이 없습니다", Toast.LENGTH_SHORT)
                }
        }
    }
}