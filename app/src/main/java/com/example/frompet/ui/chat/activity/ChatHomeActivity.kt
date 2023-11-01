package com.example.frompet.ui.chat.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.frompet.R
import com.example.frompet.ui.chat.fragment.ChatHomeFragment

class ChatHomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_home)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.chat_home_container, ChatHomeFragment())
                .commit()
        }
    }
}
