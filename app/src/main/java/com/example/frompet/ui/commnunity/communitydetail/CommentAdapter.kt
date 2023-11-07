package com.example.frompet.ui.commnunity.communitydetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.bumptech.glide.Glide
import com.example.frompet.R
import com.example.frompet.data.model.CommentData
import com.example.frompet.data.model.CommunityData
import com.example.frompet.data.model.User
import com.example.frompet.databinding.ItemReplyBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone


class CommentAdapter(private val modifyClick: (CommentData) -> Unit) :
    ListAdapter<CommentData, CommentAdapter.ViewHolder>(CommentDiffCallback) {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemReplyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val commentData = getItem(position)
        holder.bindItems(commentData)
    }


    inner class ViewHolder(private val binding: ItemReplyBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindItems(commentData: CommentData) {
            with(binding) {
                val authorUid = commentData.authorUid

                if (authorUid.isNotEmpty()) {
                    val userRef = FirebaseFirestore.getInstance().collection("User").document(authorUid)
                    userRef.get()
                        .addOnSuccessListener { userSnapshot ->
                            val user = userSnapshot.toObject(User::class.java)
                            user?.let {

                                ivPetProfile.load(user.petProfile) {
                                    error(R.drawable.sampleiamge)
                                }

                                tvPetName.text = user.petName
                            }
                        }

                }

                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA)
                sdf.timeZone = TimeZone.getTimeZone("Asia/Seoul")
                val formattedDate = sdf.format(Date(commentData.timestamp))
                tvLastTime.text = formattedDate
                tvDetailContents.text = commentData.content

                threedots3.setOnClickListener {
                    modifyClick(commentData)
                }
            }
        }
    }

    object CommentDiffCallback : DiffUtil.ItemCallback<CommentData>() {
        override fun areItemsTheSame(oldItem: CommentData, newItem: CommentData): Boolean {
            return oldItem.postDocumentId == newItem.postDocumentId
        }

        override fun areContentsTheSame(oldItem: CommentData, newItem: CommentData): Boolean {
            return oldItem == newItem
        }
    }

}
