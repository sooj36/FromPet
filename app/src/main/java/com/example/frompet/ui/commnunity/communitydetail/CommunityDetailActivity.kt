package com.example.frompet.ui.commnunity.communitydetail

import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.frompet.R
import com.example.frompet.data.model.CommentData
import com.example.frompet.data.model.CommunityData
import com.example.frompet.data.model.User
import com.example.frompet.databinding.ActivityCommunityDetailBinding
import com.example.frompet.ui.setting.FriendsListAdapter
import com.example.frompet.util.showToast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class CommunityDetailActivity : AppCompatActivity() {

    private var _binding: ActivityCommunityDetailBinding? = null
    private val binding get() = _binding!!

    private val currentUser = FirebaseAuth.getInstance().currentUser

    private val store = FirebaseFirestore.getInstance()

    private var communityData: CommunityData? = null

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CommentAdapter



    companion object {
        const val COMMUNITY_DATA = "communityData"
        const val DOCS_ID = "docsId"
    }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCommunityDetailBinding.inflate(layoutInflater)

        setContentView(binding.root)

        recyclerView = binding.rvReply
        adapter = CommentAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        communityData = intent.getParcelableExtra(DOCS_ID)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            communityData = intent.getParcelableExtra(COMMUNITY_DATA, CommunityData::class.java)
        } else {
            communityData = intent.extras?.getParcelable(COMMUNITY_DATA) as CommunityData?
        }





        communityData = intent.getParcelableExtra(DOCS_ID)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            communityData = intent.getParcelableExtra(COMMUNITY_DATA, CommunityData::class.java)

        } else {
            communityData = intent.extras?.getParcelable(COMMUNITY_DATA) as CommunityData?

        }


        // 화면에 표시
        val title = binding.tvDetailTitle
        val contents = binding.tvDetailContents
        val tag = binding.chipTag
        val lastTime = binding.tvLastTime




        // CommunityData에서 가져오기
        communityData?.let {
            title.text = it.title
            contents.text = it.contents
            tag.text = it.tag
            lastTime.text = formatDate(it.timestamp)
            loadUserData(it.uid)
        }

        binding.backBtn.setOnClickListener {
            finish()
        }

        binding.threedots.setOnClickListener {
            showPopup(it, communityData?.docsId) // 팝업 메뉴 표시
        }


        binding.btnDetailEnroll.setOnClickListener {
            // 댓글 추가 버튼 클릭 시 호출되는 함수
            addComment()
        }
        loadComments()
    }

    private fun loadUserData(uid: String) = with(binding) {
        store.collection("User").document(uid)
            .get()
            .addOnSuccessListener { docsSnapshot ->
                val user = docsSnapshot.toObject(User::class.java)
                user?.let {

                    ivPetProfile.load(user.petProfile) {
                        error(R.drawable.sampleiamge)
                    }
                    tvPetName.text = user.petName
                }
            }
            .addOnFailureListener { e ->
                showToast("사용자 정보를 불러오는데 실패했습니다.", Toast.LENGTH_SHORT)
            }
    }

    private fun formatDate(timestamp: Long?): String {
        timestamp ?: return "알 수 없음"
        val now = System.currentTimeMillis()
        val diff = now - timestamp

        val seconds = TimeUnit.MILLISECONDS.toSeconds(diff)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
        val hours = TimeUnit.MILLISECONDS.toHours(diff)
        val days = TimeUnit.MILLISECONDS.toDays(diff)

        return when {
            seconds < 60 -> "방금 전"
            minutes < 60 -> "${minutes}분 전"
            hours < 24 -> "${hours}시간 전"
            days == 1L -> "어제"
            else -> {
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                sdf.format(Date(timestamp))
            }
        }
    }

    private fun showPopup(v: View, docsId: String?) {
        val popup = PopupMenu(this, v) // 팝업 객체 선언
        menuInflater.inflate(R.menu.popup_menu, popup.menu) // 메뉴 레이아웃 inflate


        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.delete -> {
                    // 삭제
                    deleteCommunity(docsId)
                    true
                }

                R.id.cut -> {
                    updateActivity()
                    true
                }

                else -> false
            }
        }
        popup.show()
    }


    private fun updateActivity() {
        val intent: Intent = Intent(this, CommunityDetailUpdateActivity::class.java)
        intent.putExtra(COMMUNITY_DATA, communityData)
        startActivity(intent)
        finish()
    }


    // 삭제
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
                    showToast("해당 작성자만 게시글 삭제가 가능합니다", Toast.LENGTH_SHORT)
                }
        }
    }

    private fun addComment() {
        val etComments = binding.etDetailComments
        val commentText = etComments.text.toString()
        if (commentText.isNotEmpty()) {
            val authorName = currentUser?.displayName ?: ""
            val authorProfile =
                currentUser?.photoUrl?.toString() ?: ""

            val commentData = CommentData(
                content = commentText,
                authorUid = currentUser?.uid ?: "",
                authorName = authorName,
                authorProfile = authorProfile,
                postDocumentId = communityData?.docsId ?: "",
                timestamp = System.currentTimeMillis()
            )
            store.collection("Community")
                .document(communityData?.docsId ?: "")
                .collection("Comment")
                .add(commentData)
                .addOnSuccessListener {
                    showToast("댓글이 추가되었습니다", Toast.LENGTH_SHORT)
                    etComments.text.clear()

                }
                .addOnFailureListener {
                    showToast("댓글 추가에 실패했습니다", Toast.LENGTH_SHORT)
                }
        } else {
            showToast("댓글 내용을 입력하세요", Toast.LENGTH_SHORT)
        }
    }

    private fun loadComments() {
        val commentsRef = store.collection("Community")
            .document(communityData?.docsId ?: "")
            .collection("Comment")

        val query = commentsRef.orderBy("timestamp", Query.Direction.ASCENDING)

        query.addSnapshotListener { querySnapshot, error ->
            if (error != null) {
                return@addSnapshotListener
            }

            querySnapshot?.let {
                val comments = it.toObjects(CommentData::class.java)
                adapter.submitList(comments)
                Log.d("2222", "Loaded ${comments.size} comments")
            }
        }
    }

}