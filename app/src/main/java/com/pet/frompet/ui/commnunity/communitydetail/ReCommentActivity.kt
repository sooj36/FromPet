package com.pet.frompet.ui.commnunity.communitydetail
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pet.frompet.R
import com.pet.frompet.data.model.CommentData
import com.pet.frompet.data.model.CommunityData
import com.pet.frompet.data.model.ReCommentData
import com.pet.frompet.databinding.ActivityReCommentBinding
import com.pet.frompet.util.showToast

class ReCommentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReCommentBinding
    private lateinit var reCommentAdapter: ReCommentAdapter
    private lateinit var adapter: CommentAdapter
    private val store = FirebaseFirestore.getInstance()
    private var communityData: CommunityData? = null

    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReCommentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        communityData = intent.getParcelableExtra<CommunityData>("communityData")


        reCommentAdapter = ReCommentAdapter(
            { reCommentData ->

            },
            { reCommentData, imageView, textView1, textView2 ->

            }
        )

        adapter = CommentAdapter({ commentData ->
            showBottomSheet(commentData)
        }, { commentData, imageView, textView1, textView2 ->

        },{ commentData ->

        })

        binding.rvReply.adapter = adapter
        binding.rvReply.layoutManager = LinearLayoutManager(this)


        val receivedCommentData = intent.getParcelableExtra<CommentData>("commentData")

        val commentDataList = listOf(receivedCommentData)

        adapter.submitList(commentDataList)

        binding.imageButton.setOnClickListener {
            val reCommentText = binding.editTextText.text.toString()
            receivedCommentData?.let {
                if (reCommentText.isNotEmpty()) {
                    addReComment(it, reCommentText)
                } else {
                    showToast("대댓글 내용을 입력하세요", Toast.LENGTH_SHORT)
                }
            } ?: showToast("댓글 데이터를 불러오지 못했습니다", Toast.LENGTH_SHORT)
        }
        binding.backBtn.setOnClickListener {
            finish()
        }
        loadComments()
    }

    private fun addReComment(receivedCommentData: CommentData, reCommentText: String) {
        val reCommentId = store.collection("Community")
            .document(receivedCommentData.postDocumentId)
            .collection("Comment")
            .document(receivedCommentData.commentId)
            .collection("ReComment")
            .document().id

        val uid = FirebaseAuth.getInstance().currentUser?.uid

        if (uid != null) {
            val reCommentData = ReCommentData(
                reCommentId,
                receivedCommentData.commentId,
                reCommentText,
                uid,
                System.currentTimeMillis()
            )

            store.collection("Community")
                .document(receivedCommentData.postDocumentId)
                .collection("Comment")
                .document(receivedCommentData.commentId)
                .collection("ReComment")
                .document(reCommentId)
                .set(reCommentData)
                .addOnSuccessListener {
                    showToast("대댓글이 추가되었습니다", Toast.LENGTH_SHORT)
                    binding.editTextText.text.clear()
                    hideKeyboard()
                    adapter.reloadReComments(receivedCommentData)
                }
                .addOnFailureListener {
                    showToast("대댓글 추가에 실패했습니다", Toast.LENGTH_SHORT)
                }
        } else {
            showToast("사용자 정보를 불러오는데 실패했습니다.", Toast.LENGTH_SHORT)
        }
    }
    private fun showBottomSheet(commentData: CommentData) {
        val layoutId = if (commentData.authorUid == FirebaseAuth.getInstance().currentUser?.uid) {
            R.layout.bottom_sheet_layout
        } else {
            R.layout.bottom_sheet_layout2
        }

        val view = layoutInflater.inflate(layoutId, null)
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(view)

        if (layoutId == R.layout.bottom_sheet_layout) {
            val modifyTextView = view.findViewById<TextView>(R.id.bottom_sheet_modify)
            val deleteTextView = view.findViewById<TextView>(R.id.bottom_sheet_delete)

            modifyTextView.setOnClickListener {
                val intent = Intent(this, CommentModify::class.java)
                intent.putExtra("commentText", commentData.content)
                intent.putExtra("commentData", commentData)
                intent.putExtra("communityData", communityData)
                startActivity(intent)
                dialog.dismiss()
            }

            deleteTextView.setOnClickListener {
                val commentDocumentRef = store.collection("Community")
                    .document(communityData?.docsId ?: "")
                    .collection("Comment")
                    .document(commentData.commentId)
                commentDocumentRef.delete()
                    .addOnSuccessListener {
                        showToast("댓글이 삭제되었습니다", Toast.LENGTH_SHORT)
                        dialog.dismiss()
                        finish()
                    }
                    .addOnFailureListener {
                        showToast("댓글 삭제에 실패했습니다", Toast.LENGTH_SHORT)
                        dialog.dismiss()
                    }
            }
        } else {
            val reportTextView = view.findViewById<TextView>(R.id.bottom_sheet_report)

            reportTextView.setOnClickListener {
                val commentDocumentRef = store.collection("Community")
                    .document(communityData?.docsId ?: "")
                    .collection("Comment")
                    .document(commentData.commentId)
                store.runTransaction { transaction ->
                    val snapshot = transaction.get(commentDocumentRef)
                    val newReportCount = snapshot.getLong("reportCount")?.plus(1) ?: 1
                    transaction.update(commentDocumentRef, "reportCount", newReportCount)

                    // 신고 횟수가 10회 이상이면 해당 댓글 삭제, 나중에 신고는 개인 당 한 번만 할 수 있게 바꿀 예정
                    if (newReportCount >= 10) {
                        transaction.delete(commentDocumentRef)
                    }
                    null
                }.addOnSuccessListener {
                    showToast("신고가 접수되었습니다", Toast.LENGTH_SHORT)
                    dialog.dismiss()
                }.addOnFailureListener {
                    showToast("신고 접수에 실패했습니다", Toast.LENGTH_SHORT)
                    dialog.dismiss()
                }
            }
        }

        dialog.show()

        val dimView = View(this)
        dimView.setBackgroundColor(Color.parseColor("#80000000"))
        val parentLayout = findViewById<ViewGroup>(android.R.id.content)
        parentLayout.addView(dimView, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))

        dialog.setOnDismissListener {
            parentLayout.removeView(dimView)
        }
    }
    fun loadComments() {
        val receivedCommentData = intent.getParcelableExtra<CommentData>("commentData")

        store.collection("Community")
            .document(communityData?.docsId ?: "")
            .collection("Comment")
            .whereEqualTo("commentId", receivedCommentData?.commentId)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {

                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.documents.isNotEmpty()) {
                    val commentList = snapshot.documents.mapNotNull { it.toObject(CommentData::class.java) }
                    adapter.submitList(commentList)
                } else {

                }
            }
    }



}