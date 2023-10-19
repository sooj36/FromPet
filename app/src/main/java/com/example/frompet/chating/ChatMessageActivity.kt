package com.example.frompet.chating

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.frompet.chating.adapter.ChatMessageAdapter
import com.example.frompet.databinding.ActivityChatMessageBinding
import com.example.frompet.login.data.ChatMessage
import com.example.frompet.login.data.UserModel
import com.example.frompet.login.putFile
import com.example.frompet.util.showToast

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class ChatMessageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatMessageBinding
    private val chatViewModel: ChatViewModel by viewModels()
    private val adapter: ChatMessageAdapter by lazy { binding.rvMessage.adapter as ChatMessageAdapter }
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    val storage = FirebaseStorage.getInstance()
    private val typingTimeoutHandler = Handler(Looper.getMainLooper())
    private val typingTimeoutRunnable = Runnable {
        chatViewModel.setTypingStatus(false)
    }

    companion object {
        const val USER = "user"
        const val PICK_IMAGE_FROM_ALBUM = 0
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            rvMessage.adapter = ChatMessageAdapter(this@ChatMessageActivity)
            rvMessage.layoutManager = LinearLayoutManager(this@ChatMessageActivity)
        }

        chatViewModel.chatMessages.observe(this) { messages ->
            adapter.submitList(messages) {
                binding.rvMessage.post {
                    binding.rvMessage.scrollToPosition(messages.size - 1)
                }
            }
        }

        chatViewModel.isTyping.observe(this, Observer { isTyping ->
            binding.tvTyping.text = if (isTyping) "입력중..." else ""
        })

        val user: UserModel? = intent.getParcelableExtra(USER)
        user?.let {
            displayInfo(it)
            chatViewModel.checkTypingStatus(it.uid)

            val currentUserId = auth.currentUser?.uid ?: return
            val chatRoomId = chatViewModel.chatRoom(currentUserId, user.uid)

            binding.ivSendBtn.setOnClickListener {
                val message = binding.etMessage.text.toString()
                if (message.isNotEmpty()) {
                    chatViewModel.sendMessage(user.uid, message)
                    binding.etMessage.text.clear()
                }
            }

            binding.etMessage.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    if (s.isNullOrEmpty()) {
                        chatViewModel.setTypingStatus(false)
                    } else {
                        chatViewModel.setTypingStatus(true)
                        typingTimeoutHandler.removeCallbacks(typingTimeoutRunnable)
                        typingTimeoutHandler.postDelayed(typingTimeoutRunnable, 5000)
                    }
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })
            chatViewModel.loadPreviousMessages(chatRoomId)
        }

        binding.backBtn.setOnClickListener {
            goneNewMessage()
            finish()
        }
        binding.ivSendImage.setOnClickListener {
            goGallery()
        }

    }
    override fun onBackPressed() {
        goneNewMessage()
        super.onBackPressed()
    }
    override fun onDestroy() {
        goneNewMessage()
        super.onDestroy()
    }
    private fun goneNewMessage() {
        val user: UserModel? = intent.getParcelableExtra(USER)
        user?.let {
            val currentUserId = auth.currentUser?.uid ?: return
            val chatRoomId = chatViewModel.chatRoom(currentUserId, user.uid)
            chatViewModel.goneNewMessages(chatRoomId)
        }
    }



    private fun goGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK)
        galleryIntent.type = "image/*"
        startActivityForResult(galleryIntent, PICK_IMAGE_FROM_ALBUM)
    }

    private fun displayInfo(user: UserModel) {
        binding.tvFriendName.text = user.petName
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_FROM_ALBUM && resultCode == Activity.RESULT_OK) {
            val photoUri = data?.data
            uploadImageToStroge(photoUri)
        }
    }

    private fun uploadImageToStroge(photoUri: Uri?) {
        contentUpload(photoUri.toString())
    }

    private fun contentUpload(uri: String?) {
        uri?.let { petProfileUri ->
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "IMAGE_$timestamp.png"
            // 서버 스토리지에 접근하기
            val storageRef = storage.reference.child("images").child(fileName)
            // 서버 스토리지에 파일 업로드하기
            storageRef.putFile(petProfileUri)
                .continueWithTask { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let { throw it }
                    }
                    storageRef.downloadUrl
                }
                .addOnSuccessListener { uri ->
                    val imageUrl = uri.toString()
                    val currentUserId = auth.currentUser?.uid
                   showToast("이미지 업로드 성공",Toast.LENGTH_LONG)
                    firestore.collection("User").document(currentUserId!!)
                        .get()
                        .addOnSuccessListener { document ->
                            val currentUser = document.toObject(UserModel::class.java)
                            val currentUserPetName = currentUser?.petName

                    val user: UserModel? = intent.getParcelableExtra(USER)
                    user?.let {
                        val message = ChatMessage(
                            senderId = currentUserId,
                            receiverId = user.uid,
                            senderPetName = currentUserPetName?:return@let,
                            message = "",
                            imageUrl = imageUrl,
                            timestamp = System.currentTimeMillis()
                        )
                        chatViewModel.sendImage(message)
                    }
                }}
        }
    }
}