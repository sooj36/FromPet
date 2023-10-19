package com.example.frompet.ui.chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import coil.load
import com.example.frompet.R
import com.example.frompet.databinding.ActivityChatMessageBinding
import com.example.frompet.databinding.ActivityChatPullScreenBinding

class ChatPullScreenActivity : AppCompatActivity() {
    companion object {
        const val IMAGE_URL = "image_url"
    }

    private lateinit var binding: ActivityChatMessageBinding

    // 현재 툴바가 보이는지
    private var isToolbarVisible = true
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_pull_screen)

        val imageUrl = intent.getStringExtra(IMAGE_URL)
        val imageView: ImageView = findViewById(R.id.iv_full_screen)
        imageView.load(imageUrl) {
            error(R.drawable.kakaotalk_20230825_222509794_01)
        }
        // 처음에 툴바 보임
        val toolbar: View = findViewById(R.id.toolbar)
        toolbar.visibility = View.VISIBLE

        val contentContainer: View = findViewById(R.id.iv_full_screen)
        contentContainer.setOnClickListener {
            // 화면을 터치할 때마다 바뀌기
            isToolbarVisible = !isToolbarVisible
            toggleToolbarVisibility()
        }
        val btnBack: ImageView = findViewById(R.id.btnBack)
        btnBack.setOnClickListener {
            onBackPressed()
        }
    }
    // 툴바 터치 할 때마다 보여지고 안 보여지는 함수!
    private fun toggleToolbarVisibility() {
        val toolbar: View = findViewById(R.id.toolbar)
        if (isToolbarVisible) {
            // 툴바 보이기
            toolbar.visibility = View.VISIBLE
        } else {
            // 툴바 안 보여지기
            toolbar.visibility = View.GONE
        }
    }
}