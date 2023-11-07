package com.example.frompet.ui.commnunity.communitydetail
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.frompet.R
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.example.frompet.data.model.CommentData
import com.example.frompet.data.model.CommunityData
import com.google.firebase.firestore.FirebaseFirestore


class CommentModify : AppCompatActivity() {
    private lateinit var etComment: EditText

    // 추가: 댓글 수정 시 사용할 CommentData
    private var commentData: CommentData? = null
    private var communityData: CommunityData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment_modify)

        etComment = findViewById(R.id.etComment)

        // CommentModify 액티비티로 전달된 데이터 가져오기
        commentData = intent.getParcelableExtra("commentData")
        communityData = intent.getParcelableExtra("communityData") // 추가: 관련 CommunityData 가져오기

        val commentText = intent.getStringExtra("commentText")
        etComment.setText(commentText)

        val btModify = findViewById<Button>(R.id.bt_modify2)
        btModify.setOnClickListener {
            val modifiedComment = etComment.text.toString()

            if (modifiedComment.isNotEmpty()) {
                // 데이터베이스 업데이트
                val store = FirebaseFirestore.getInstance()
                val commentDocumentRef = store.collection("Community")
                    .document(communityData?.docsId ?: "")
                    .collection("Comment")
                    .document(commentData?.commentId ?: "") // 수정된 부분

                commentDocumentRef.update("content", modifiedComment)
                    .addOnSuccessListener {
                        // 데이터베이스 업데이트 성공 시
                        val dataIntent = Intent().apply {
                            putExtra("modifiedComment", modifiedComment)
                        }
                        setResult(Activity.RESULT_OK, dataIntent)
                        finish()
                    }
                    .addOnFailureListener {
                        // 데이터베이스 업데이트 실패 시
                        // 처리할 내용 추가
                    }
            } else {
                // 수정 내용이 비어 있을 경우 사용자에게 메시지 표시
                // 처리할 내용 추가
            }
        }

        val backBtn = findViewById<ImageView>(R.id.back_btn)
        backBtn.setOnClickListener {
            finish()
        }
    }
}
