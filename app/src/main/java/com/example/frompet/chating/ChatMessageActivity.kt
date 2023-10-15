package com.example.frompet.chating

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.frompet.chating.adapter.ChatMessageAdapter
import com.example.frompet.databinding.ActivityChatMessageBinding
import com.example.frompet.login.data.ChatMessage
import com.example.frompet.login.data.UserModel
import com.example.frompet.login.viewmodel.ChatViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class ChatMessageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatMessageBinding
    private val viewModel: ChatViewModel by viewModels()
    private val adapter = ChatMessageAdapter(FirebaseAuth.getInstance().currentUser?.uid ?: "")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvMessage.adapter = adapter
        binding.rvMessage.layoutManager = LinearLayoutManager(this)

        viewModel.chatMessages.observe(this) { messages ->
            adapter.submitList(messages) {
                binding.rvMessage.post {//맨마지막채팅이 맨 아래에 오게하긔
                    binding.rvMessage.scrollToPosition(messages.size - 1)
                }
            }
        }

        val user: UserModel? = intent.getParcelableExtra("user")
        user?.let {
            binding.ivSendBtn.setOnClickListener {
                val message = binding.etMessage.text.toString()
                if (message.isNotEmpty()) {
                    viewModel.sendMessage(user.uid, message)
                    binding.etMessage.text.clear()
                }
            }
        }
        viewModel.loadPreviousMessages()
    }
}
