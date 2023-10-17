package com.example.frompet.chating

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.frompet.chating.adapter.ChatMessageAdapter
import com.example.frompet.databinding.ActivityChatMessageBinding
import com.example.frompet.login.data.UserModel
import com.example.frompet.login.viewmodel.ChatViewModel
import com.google.firebase.auth.FirebaseAuth

class ChatMessageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatMessageBinding
    private val viewModel: ChatViewModel by viewModels()
    private val adapter = ChatMessageAdapter(FirebaseAuth.getInstance().currentUser?.uid ?: "")
    private val auth = FirebaseAuth.getInstance()
    private val typingTimeoutHandler = Handler(Looper.getMainLooper())
    private val typingTimeoutRunnable = Runnable {
        viewModel.setTypingStatus(false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvMessage.adapter = adapter
        binding.rvMessage.layoutManager = LinearLayoutManager(this)

        viewModel.chatMessages.observe(this) { messages ->
            adapter.submitList(messages) {
                binding.rvMessage.post {
                    binding.rvMessage.scrollToPosition(messages.size - 1)
                }
            }
        }

        viewModel.isTyping.observe(this, Observer { isTyping ->
            binding.tvTyping.text = if (isTyping) "입력중..." else ""
        })

        val user: UserModel? = intent.getParcelableExtra("user")
        user?.let {
            displayInfo(it)
            viewModel.checkTypingStatus(it.uid)

            val currentUserId = auth.currentUser?.uid ?: return
            val chatRoomId = viewModel.chatRoom(currentUserId, user.uid)

            binding.ivSendBtn.setOnClickListener {
                val message = binding.etMessage.text.toString()
                if (message.isNotEmpty()) {
                    viewModel.sendMessage(user.uid, message)
                    binding.etMessage.text.clear()
                }
            }

            binding.etMessage.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    if (s.isNullOrEmpty()) {
                        viewModel.setTypingStatus(false)
                    } else {
                        viewModel.setTypingStatus(true)
                        typingTimeoutHandler.removeCallbacks(typingTimeoutRunnable)
                        typingTimeoutHandler.postDelayed(typingTimeoutRunnable, 5000)
                    }
                }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })
            viewModel.loadPreviousMessages(chatRoomId)
        }

        binding.backBtn.setOnClickListener {
            finish()
        }
    }

    private fun displayInfo(user: UserModel) {
        binding.tvFriendName.text = user.petName
    }
}
