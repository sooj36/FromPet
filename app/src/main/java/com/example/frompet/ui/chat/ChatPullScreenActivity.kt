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

    private lateinit var binding: ActivityChatPullScreenBinding //이렇게해도 잘댐
    // 현재 툴바가 보이는지
    private var isToolbarVisible = true

    override fun onCreate(savedInstanceState: Bundle?) {
         binding = ActivityChatPullScreenBinding.inflate(layoutInflater)

        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val imageUrl = intent.getStringExtra(IMAGE_URL)

        binding.ivFullScreen.load(imageUrl)

        // 처음에 툴바 보임

        binding.toolbar.visibility = View.VISIBLE


        binding.ivFullScreen.setOnClickListener {
            // 화면을 터치할 때마다 바뀌기
            isToolbarVisible = !isToolbarVisible
            toggleToolbarVisibility()
        }

        binding.btnBack.setOnClickListener {
            onBackPressed()
        }
    }
    // 툴바 터치 할 때마다 보여지고 안 보여지는 함수!
    private fun toggleToolbarVisibility() {

        if (isToolbarVisible) {
            // 툴바 보이기
            binding.toolbar.visibility = View.VISIBLE
        } else {
            // 툴바 안 보여지기
            binding.toolbar.visibility = View.GONE
        }
    }
}