package com.example.frompet.chating

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.frompet.R
import com.example.frompet.chating.adapter.ChatListAdapter
import com.example.frompet.chating.adapter.ChatMessageAdapter
import com.example.frompet.databinding.ActivityChatMessageBinding
import com.example.frompet.login.data.ChatMessage
import com.example.frompet.login.data.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class ChatMessageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatMessageBinding
    private val adapter = ChatMessageAdapter(FirebaseAuth.getInstance().currentUser?.uid ?: "")
    private val database = FirebaseDatabase.getInstance().reference
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityChatMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvMessage.adapter = adapter
        binding.rvMessage.layoutManager = LinearLayoutManager(this)

        val user: UserModel? = intent.getParcelableExtra("user")
        user?.let {
            binding.ivSendBtn.setOnClickListener {
                val message = binding.etMessage.text.toString()
                if (message.isNotEmpty()) {
                    sendMessage(user.uid, message)
                }
            }
        }
        loadPreviousMessages()
    }

    private fun sendMessage(receiverId: String, message: String) {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        firestore.collection("User").document(currentUserId).get()
            .addOnSuccessListener { document ->
                val currentUser = document.toObject(UserModel::class.java)
                val senderPetName = currentUser?.petName ?: "Unknown"

                val chatMessage = ChatMessage(
                    senderId = currentUserId,
                    senderPetName = senderPetName,
                    receiverId = receiverId,
                    message = message,
                    timestamp = System.currentTimeMillis()
                )

                database.child("chatMessages").push().setValue(chatMessage)
                    .addOnSuccessListener {
                        binding.etMessage.text.clear()
                        adapter.submitList(adapter.currentList + chatMessage)
                    }
                    .addOnFailureListener { exception ->
                        Log.d("jun", "메시지전송실패: ${exception.message}")
                    }
            }
            .addOnFailureListener { exception ->
                Log.d("jun", "연결실패: ${exception.message}")
            }
    }

    private fun loadPreviousMessages() {
        val currentUserId = auth.currentUser?.uid ?: return
        //박세준
        // 현재 사용자가 관련된 모든 메시지를 불러오기 위한 쿼리입니다
        database.child("chatMessages").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val allMessages = dataSnapshot.children.mapNotNull { it.getValue(ChatMessage::class.java) }
                    .filter { it.senderId == currentUserId || it.receiverId == currentUserId }
                adapter.submitList(allMessages.sortedBy { it.timestamp })
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("jun", "불러오기실패: ${databaseError.message}")
            }
        })
    }

}
