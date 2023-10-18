package com.example.frompet.chating

import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import coil.load
import com.example.frompet.R
import com.example.frompet.databinding.ActivityChatMessageBinding
import com.example.frompet.databinding.ActivityChatPullScreenBinding

class ChatPullScreenActivity : AppCompatActivity() {
    companion object{
        const val IMAGE_URL = "image_url"
    }

    private lateinit var binding: ActivityChatMessageBinding
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_pull_screen)

        val imageUrl = intent.getStringExtra(IMAGE_URL)
        val imageView :ImageView = findViewById(R.id.iv_full_screen)
        imageView.load(imageUrl){
            error(R.drawable.kakaotalk_20230825_222509794_01)
        }

        val backBtn = findViewById<ImageView>(R.id.back_btn)
        backBtn.setOnClickListener {
            finish()
        }

    }

}