package com.pet.frompet.ui.commnunity.communitydetail

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.pet.frompet.R
import com.pet.frompet.data.model.CommunityData
import com.pet.frompet.databinding.ActivityCommunityDetailUpdateBinding
import com.pet.frompet.util.showToast
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
            communityData = intent.getParcelableExtra(
                CommunityDetailActivity.COMMUNITY_DATA,
                CommunityData::class.java
            )
        } else {
            communityData =
                intent.extras?.getParcelable(CommunityDetailActivity.COMMUNITY_DATA) as CommunityData?
        }
        Log.d("sooj", "데이터 ${communityData}")




        with(binding) {

            updateTitle.setText(communityData?.title)
            updateContents.setText(communityData?.contents)

            backBtn.setOnClickListener {
                finish()
            }

            when (communityData?.tag) {
                "나눔" -> chipGroup.check(R.id.chip_share)
                "산책" -> chipGroup.check(R.id.chip_walk)
                "사랑" -> chipGroup.check(R.id.chip_love)
                "정보교환" -> chipGroup.check(R.id.chip_exchange)
            }

            chipGroup.setOnCheckedChangeListener { group, checkedId ->

            }
            btnDone.setOnClickListener {
                updateCommunity(communityData?.docsId)
            }
        }
    }

    private fun updateCommunity(docsId: String?) {
        if (docsId != null) { // 널이면 add
            store.collection("Community")
                .document(docsId)
                .update(
                    "title",
                    binding.updateTitle.text.toString(),
                    "contents",
                    binding.updateContents.text.toString(),
                    "tag",
                    when (binding.chipGroup.checkedChipId) {
                        R.id.chip_share -> "나눔"
                        R.id.chip_walk -> "산책"
                        R.id.chip_love -> "사랑"
                        R.id.chip_exchange -> "정보교환"
                        else -> ""
                    }


                )
                .addOnSuccessListener {
                    showToast("게시글이 수정되었습니다", Toast.LENGTH_SHORT)
                    finish()
                }
                .addOnFailureListener {
                    showToast("게시글 수정 권한이 없습니다", Toast.LENGTH_SHORT)
                }
        }
    }
}