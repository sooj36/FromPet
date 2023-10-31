package com.example.frompet.ui.commnunity.communityadd

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.frompet.R
import com.example.frompet.data.model.CommunityData
import com.example.frompet.data.model.User
import com.example.frompet.databinding.ActivityCommunityAddBinding
import com.example.frompet.ui.chat.activity.ChatMessageActivity
import com.example.frompet.ui.chat.dialog.ChatExitDialog
import com.example.frompet.ui.commnunity.AddExitDialog
import com.example.frompet.ui.commnunity.community.CommunityActivity
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCommunityAddBinding.inflate(layoutInflater)

        setContentView(binding.root)

        initView()

        binding.btnAddEnroll.setOnClickListener {
            val titleText = binding.etAddTitle.text.toString()
            val contentsText = binding.etAddContents.text.toString()
            val tagShare = binding.chipShare.tag
            val tagWalk = binding.chipWalk.tag
            val tagLove = binding.chipLove.tag
            val tagExchange = binding.chipExchange.tag
            val tagAdd = binding.chipAdd.tag

            title = titleText
            contents = contentsText

            // Firebase 현재 사용자 가져오기
            val currentUser = FirebaseAuth.getInstance().currentUser

            if (currentUser != null) {
                if (title.isEmpty() || contents.isEmpty()) {
                    showToast(getString(R.string.commu_all_add_plz), Toast.LENGTH_SHORT)
                    return@setOnClickListener

                } else {
                    val currentTime = System.currentTimeMillis()
                    timeStamp = currentTime.toString()
                }

                // Community 모델 생성
                val community = CommunityData(
                    title = title,
                    tag = tag,
                    contents = contents,
                    timestamp = timeStamp,
                    uid = currentUser.uid
                )
//                community.uid = currentUser.uid


                //커뮤니티액티비티로 옮김
                FirebaseFirestore.getInstance().collection("Community")
                    .document(currentUser.uid)
                    .set(community)  // add를
                    .addOnSuccessListener {
                        Toast.makeText(this, "등록되었습니다", Toast.LENGTH_SHORT).show()

                        val dataIntent = Intent()
                        dataIntent.putExtra("","")
                        setResult(RESULT_OK, dataIntent)
                        finish()
                    }
                    .addOnFailureListener {
                        // 정보 저장 실패
                        Toast.makeText(this, "등록에 실패하였습니다.", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    private fun initView() {
        with(binding) {
            btnAddCancel.setOnClickListener { showExitDialog() }
            backBtn.setOnClickListener { backToCommunity()}
        }    }

    private fun backToCommunity() {
        finish()
    }

    private fun showExitDialog() {
        AddExitDialog(this).showExitDialog {
            finish()
        }
    }
}