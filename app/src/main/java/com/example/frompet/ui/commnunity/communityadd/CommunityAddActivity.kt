package com.example.frompet.ui.commnunity.communityadd

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.frompet.R
import com.example.frompet.data.model.CommunityData
import com.example.frompet.data.model.User
import com.example.frompet.databinding.ActivityCommunityAddBinding
import com.example.frompet.ui.commnunity.AddExitDialog
import com.example.frompet.util.showToast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class CommunityAddActivity : AppCompatActivity() {

    private var _binding: ActivityCommunityAddBinding? = null
    private val binding get() = _binding!!

    // FirebaseStorage 초기화
    val storage = FirebaseStorage.getInstance()

    // data class
    private var title: String = "" // 제목
    private var tag: String = "" // 카테고리
    private var timeStamp: String = "" // 시간
    private var contents: String = "" // 내용
    private var docsId: String? = null // 문서id
    private var user: User? = null


    companion object {
        const val DOCS_ID = "docsId"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCommunityAddBinding.inflate(layoutInflater)

        setContentView(binding.root)

        initView()


        binding.btnAddEnroll.setOnClickListener {
            val titleText = binding.etAddTitle.text.toString()
            val contentsText = binding.etAddContents.text.toString()

            title = titleText
            contents = contentsText

            // Firebase 현재 사용자 가져오기
            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser != null) {
                val userUID = currentUser.uid
                val firestore = FirebaseFirestore.getInstance()
                val userRef = firestore.collection("User").document(userUID)

                userRef.get()
                    .addOnSuccessListener { documentSnapshot ->
                        if (documentSnapshot.exists()) {
                            val petType = documentSnapshot.getString("petType")
                            if (petType != null) {
                                val currentTime = System.currentTimeMillis()
                                timeStamp = currentTime.toString()
                                // Community 모델 생성
                                val community = CommunityData(
                                    title = title,
                                    tag = tag,
                                    contents = contents,
                                    timestamp = timeStamp,
                                    uid = currentUser.uid,
                                    docsId = docsId,
                                    petType = petType
                                )

                                //커뮤니티액티비티로 옮김
                                val communityCollection =
                                    FirebaseFirestore.getInstance().collection("Community")
                                communityCollection
                                    .add(community)
                                    .addOnSuccessListener { docId ->
                                        community.docsId = docId.id
                                        docId.set(community)
                                        Toast.makeText(this, "등록되었습니다", Toast.LENGTH_SHORT).show()

                                        val dataIntent = Intent()
                                        dataIntent.putExtra(DOCS_ID, community.docsId)
                                        setResult(RESULT_OK, dataIntent)
                                        finish()
                                    }
                                    .addOnFailureListener {
                                        // 정보 저장 실패
                                        Toast.makeText(this, "등록에 실패하였습니다.", Toast.LENGTH_SHORT)
                                            .show()
                                    }
                            }
                        }
                    }
            }
        }


    }

    private fun initView() {
        with(binding) {
            btnAddCancel.setOnClickListener { showExitDialog() }
            backBtn.setOnClickListener { backToCommunity() }
        }
    }

    private fun backToCommunity() {
        finish()
    }

    private fun showExitDialog() {
        AddExitDialog(this).showExitDialog {
            finish()
        }
    }




}