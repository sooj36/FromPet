package com.example.frompet.ui.commnunity.communitydetail

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.PopupMenu
import com.example.frompet.R
import com.example.frompet.data.model.CommunityData
import com.example.frompet.databinding.ActivityCommunityDetailBinding
import com.example.frompet.ui.commnunity.community.CommunityActivity
import com.example.frompet.util.showToast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CommunityDetailActivity : AppCompatActivity() {

    private var _binding: ActivityCommunityDetailBinding? = null
    private val binding get() = _binding!!

    private val currentUser = FirebaseAuth.getInstance().currentUser

    private val store = FirebaseFirestore.getInstance()


    companion object {
        const val COMMUNITY_DATA = "communityData"
    }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCommunityDetailBinding.inflate(layoutInflater)


        setContentView(binding.root)

        // Intent에서 데이터 가져옴
        var communityData: CommunityData? = intent.getParcelableExtra("docsId")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            communityData = intent.getParcelableExtra(COMMUNITY_DATA, CommunityData::class.java)
        } else {
            communityData = intent.getExtras()?.getSerializable(COMMUNITY_DATA) as CommunityData?
        }


        // 화면에 표시
        val title = binding.tvDetailTitle
        val contents = binding.tvDetailContents

        // CommunityData에서 가져오기
        title.text = communityData?.title
        contents.text = communityData?.contents

        binding.backBtn.setOnClickListener {
            val intent: Intent = Intent(this@CommunityDetailActivity, CommunityActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.threedots.setOnClickListener {
            showPopup(it, communityData?.docsId) // 팝업 메뉴 표시
        }

    }

    private fun showPopup(v: View, docsId: String?) {
        val popup = PopupMenu(this, v) // 팝업 객체 선언
        menuInflater.inflate(R.menu.popup_menu, popup.menu) // 메뉴 레이아웃 inflate


        // 람다식으로 처리
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.delete -> {
                    // 삭제
                    deleteCommunity(docsId)
                    true
                }

                R.id.cut -> {
                    // cut 항목에 대한 처리 추가
                    true
                }

                else -> false
            }
        }
        popup.show()
    }


    private fun deleteCommunity(docsId: String?) {
        if (docsId != null) {
            store.collection("Community")
                .document(docsId)
                .delete()
                .addOnSuccessListener {
                    showToast("게시글이 삭제되었습니다", Toast.LENGTH_SHORT)
                    finish()
                }
                .addOnFailureListener {
                    showToast("게시글이 삭제가 실패하였습니다", Toast.LENGTH_SHORT)
                }
        }
    }
}