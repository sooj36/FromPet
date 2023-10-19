package com.example.frompet.chating.adapter

import android.content.Context
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.frompet.R
import com.example.frompet.chating.ChatClickUserDetailActivity
import com.example.frompet.chating.ChatPullScreenActivity
import com.example.frompet.chating.ChatUserDetailActivity

import com.example.frompet.databinding.ItemMyMessageBinding
import com.example.frompet.databinding.ItemOtherMessageBinding
import com.example.frompet.login.data.ChatMessage
import com.example.frompet.login.data.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date
import java.util.Locale


class ChatMessageAdapter(var context: Context) :
    ListAdapter<ChatMessage, RecyclerView.ViewHolder>(DiffCallback()) {

    private val firestore = FirebaseFirestore.getInstance()

    companion object {
        const val IMAGE_URL = "image_url"
        const val VIEW_TYPE_MY_MESSAGE = 1
        const val VIEW_TYPE_OTHER_MESSAGE = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_MY_MESSAGE -> {
                val binding = ItemMyMessageBinding.inflate(inflater, parent, false)
                MyMessageViewHolder(binding)
            }

            VIEW_TYPE_OTHER_MESSAGE -> {
                val binding = ItemOtherMessageBinding.inflate(inflater, parent, false)
                OtherMessageViewHolder(binding)
            }

            else -> throw IllegalArgumentException("")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val chatMessage = getItem(position)
        when (holder) {
            is MyMessageViewHolder -> holder.bind(chatMessage)
            is OtherMessageViewHolder -> holder.bind(chatMessage)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val chatMessage = getItem(position)
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        return if (chatMessage.senderId == currentUserId) {
            VIEW_TYPE_MY_MESSAGE
        } else {
            VIEW_TYPE_OTHER_MESSAGE
        }
    }

    inner class MyMessageViewHolder(
        private val binding: ItemMyMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(chatMessage: ChatMessage) {
            binding.apply { //리턴값이 없으면 with thi로 빼는게 맞다
                tvMessage.setBackgroundResource(R.drawable.chat2)
                ivMessageImage.setBackgroundResource(R.drawable.chat2)
                tvMessage.text = chatMessage.message
                tvTime.text = SimpleDateFormat(
                    "a HH:mm",
                    Locale.KOREA
                ).format(Date(chatMessage.timestamp)) //뷰모델에서 해야한다 ui에 바인딩시킬요소를 뷰모델에서 가져와야한다

                if (chatMessage.imageUrl.isNullOrEmpty().not()) { //!부정보단 not으로 코드 가독성 코틀린이다
                    ivMessageImage.isVisible = true
                    tvMessage.isVisible = false

                    ivMessageImage.load(chatMessage.imageUrl) {
                        error(R.drawable.kakaotalk_20230825_222509794_01)
                    }
                    clickListener(ivMessageImage, chatMessage.imageUrl)
                } else {
                    ivMessageImage.isVisible = false
                }
            }
        }
    } //메시지를 봤을때 같은조건일때 현재데이터서부터for을 돌려서 같은시간대 아닌거  filter 끝데이터랑 시간이 같은경우 시간표시 아닌경우
    // 다표시데이터도 봐줘야하고 시간?을봐야겠네?.같으면 시간을 표시 x 마지막 풍선에 시간을표시

    inner class OtherMessageViewHolder( // inner클래스 x 고려해보자
        private val binding: ItemOtherMessageBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(chatMessage: ChatMessage) {
            binding.apply {
                tvName.text = chatMessage.senderPetName
                tvMessage.setBackgroundResource(R.drawable.chat1)
                ivMessageImage.setBackgroundResource(R.drawable.chat1)
                tvMessage.text = chatMessage.message
                tvTime.text =
                    SimpleDateFormat("a HH:mm", Locale.KOREA).format(Date(chatMessage.timestamp))

                if (chatMessage.imageUrl.isNullOrEmpty().not()) {
                    ivMessageImage.isVisible = true
                    tvMessage.isVisible = false
                    ivMessageImage.load(chatMessage.imageUrl) {
                        error(R.drawable.kakaotalk_20230825_222509794_01)
                    }
                    clickListener(ivMessageImage, chatMessage.imageUrl)
                } else {
                    ivMessageImage.isVisible = false
                }
                firestore.collection("User").document(chatMessage.senderId)
                    .get()
                    .addOnSuccessListener { document ->
                        val user = document.toObject(UserModel::class.java)
                        user?.petProfile?.let {
                            ivProfile.load(it) {
                                error(R.drawable.kakaotalk_20230825_222509794_01)
                                ivProfile.setOnClickListener { userDetail(user) }
                            }

                        }
                    }
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<ChatMessage>() {
        override fun areItemsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
            return oldItem.senderPetName == newItem.senderPetName
        }

        override fun areContentsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
            return oldItem == newItem
        }
    }

    private fun clickListener(imageView: View, imageUrl: String?) {
        imageView.setOnClickListener {
            val intent = Intent(it.context, ChatPullScreenActivity::class.java)
            intent.putExtra(IMAGE_URL, imageUrl)
            it.context.startActivity(intent)
        }
    }

    private fun userDetail(user: UserModel) {
        val intent = Intent(context, ChatClickUserDetailActivity::class.java)
        intent.putExtra(ChatClickUserDetailActivity.USER, user)
        context.startActivity(intent)
    }

}
