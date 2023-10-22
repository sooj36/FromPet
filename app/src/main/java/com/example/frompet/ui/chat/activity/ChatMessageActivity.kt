package com.example.frompet.ui.chat.activity

import com.example.frompet.ui.chat.dialog.ChatExitDailog
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.frompet.MatchSharedViewModel
import com.example.frompet.ui.chat.adapter.ChatMessageAdapter
import com.example.frompet.databinding.ActivityChatMessageBinding
import com.example.frompet.data.model.User
import com.example.frompet.ui.chat.viewmodel.ChatViewModel
import com.example.frompet.ui.chat.viewmodel.MessageViewModel


import com.google.firebase.auth.FirebaseAuth



class ChatMessageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatMessageBinding
    private val messageViewModel: MessageViewModel by viewModels()
    private val chatViewModel: ChatViewModel by viewModels()
    private val matchSharedViewModel: MatchSharedViewModel by viewModels()

    private lateinit var adapter: ChatMessageAdapter
    private val auth = FirebaseAuth.getInstance()
    private val typingTimeoutHandler = Handler(Looper.getMainLooper())
    private val typingTimeoutRunnable = Runnable {
        messageViewModel.setTypingStatus(false)
    }

    companion object {
        const val USER = "user"
        const val PICK_IMAGE_FROM_ALBUM = 0
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        observeViewModels()

        val user: User? = intent.getParcelableExtra(USER)
        user?.let { handleChatActions(it) }

        binding.backBtn.setOnClickListener {
            goneNewMessage()
            finish()
        }
        binding.ivExit.setOnClickListener {
            showExitDailog()
        }

        binding.ivSendImage.setOnClickListener { goGallery() }
    }
      override fun onBackPressed() {
        goneNewMessage()
        super.onBackPressed()
    }

    override fun onDestroy() {
        goneNewMessage()
        super.onDestroy()
    }
    private fun setupRecyclerView() {
        adapter = ChatMessageAdapter(this@ChatMessageActivity)
        binding.apply {
            rvMessage.adapter = adapter
            val layoutManager = LinearLayoutManager(this@ChatMessageActivity)
            layoutManager.stackFromEnd = true
            rvMessage.layoutManager = layoutManager
        }
    }

    private fun observeViewModels() {
        messageViewModel.chatMessages.observe(this) { messages ->
            adapter.submitList(messages) {
                binding.rvMessage.post {
                    binding.rvMessage.scrollToPosition(messages.size - 1)
                }
            }
        }

        messageViewModel.isTyping.observe(this, Observer { isTyping ->
            binding.tvTyping.text = if (isTyping) "입력중..." else ""
        })
    }
    private fun showExitDailog() {
        ChatExitDailog(this).showExitDailog {
            val user: User? = intent.getParcelableExtra(USER)
            user?.let { selectUser->
                val currentUserId = auth.currentUser?.uid ?: return@let
                val chatRoomId = messageViewModel.chatRoom(currentUserId, selectUser.uid)
                matchSharedViewModel.removeMatchedUser(selectUser.uid)
                chatViewModel.removeChatRoom(chatRoomId)
                finish()
            }
        }
    }

    private fun handleChatActions(user: User) {
        displayInfo(user)
        messageViewModel.checkTypingStatus(user.uid)

        val currentUserId = auth.currentUser?.uid ?: return
        val chatRoomId = messageViewModel.chatRoom(currentUserId, user.uid)
        messageViewModel.observeChatMessages(chatRoomId)
        messageViewModel.observeTypingStatus(user.uid)
        messageViewModel.observeUserProfile(user.uid)

        binding.ivSendBtn.setOnClickListener {
            val message = binding.etMessage.text.toString()
            if (message.isNotEmpty()) {
                messageViewModel.sendMessage(user.uid, message)
                binding.etMessage.text.clear()
            }
        }

        binding.etMessage.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()) {
                    messageViewModel.setTypingStatus(false)
                } else {
                    messageViewModel.setTypingStatus(true)
                    typingTimeoutHandler.removeCallbacks(typingTimeoutRunnable)
                    typingTimeoutHandler.postDelayed(typingTimeoutRunnable, 5000)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        messageViewModel.loadPreviousMessages(chatRoomId)
    }

    private fun goneNewMessage() {
        val user: User? = intent.getParcelableExtra(USER)
        user?.let {
            val currentUserId = auth.currentUser?.uid ?: return
            val chatRoomId = messageViewModel.chatRoom(currentUserId, user.uid)
            messageViewModel.goneNewMessages(chatRoomId)
        }
    }

    private fun goGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK)
        galleryIntent.type = "image/*"
        startActivityForResult(galleryIntent, PICK_IMAGE_FROM_ALBUM)
    }

    private fun displayInfo(user: User) {
        binding.tvFriendName.text = user.petName
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_FROM_ALBUM && resultCode == Activity.RESULT_OK) {
            val photoUri = data?.data
            photoUri?.let {
                val user: User? = intent.getParcelableExtra(USER)
                user?.let { selectedUser ->
                    messageViewModel.uploadImage(it, selectedUser)
                }
            }
        }
    }
}