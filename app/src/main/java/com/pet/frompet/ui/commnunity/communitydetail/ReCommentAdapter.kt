package com.pet.frompet.ui.commnunity.communitydetail

import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pet.frompet.R
import com.pet.frompet.data.model.CommentData
import com.pet.frompet.data.model.ReCommentData
import com.pet.frompet.data.model.User
import com.pet.frompet.databinding.ItemReReplyBinding
import com.pet.frompet.ui.map.MapUserDetailActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class ReCommentAdapter(
    private val modifyClick: (ReCommentData) -> Unit,
    private val likeClick: (ReCommentData, ImageView, TextView, TextView) -> Unit,
) : ListAdapter<ReCommentData, ReCommentAdapter.ViewHolder>(ReCommentDiffCallback) {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemReReplyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val reCommentData = getItem(position)
        Log.d("ReCommentAdapter", "Binding reComment: $reCommentData")
        holder.bindItems(reCommentData)
    }

    inner class ViewHolder(private val binding: ItemReReplyBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindItems(reCommentData: ReCommentData) {
            with(binding) {
                val authorUid = reCommentData.authorUid
                val uid = FirebaseAuth.getInstance().currentUser?.uid
                val likeUsers = reCommentData.likeUsers
                val likeCount = reCommentData.likeCount

                if (likeUsers.contains(uid)) {
                    textView11.text = likeCount.toString()
                    textView11.setTextColor(Color.parseColor("#FF88C1"))
                    textView9.setTextColor(Color.parseColor("#FF88C1"))
                    ivThumbsUp.setImageResource(R.drawable.icon_sel_thumb)
                } else {
                    textView11.text = if (likeCount > 0) likeCount.toString() else ""
                    textView11.setTextColor(Color.parseColor("#000000"))
                    textView9.setTextColor(Color.parseColor("#000000"))
                    ivThumbsUp.setImageResource(R.drawable.icon_unsel_thumb)
                }

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
                                ivPetProfile.setOnClickListener {
                                    val context = it.context
                                    val intent = Intent(context, MapUserDetailActivity::class.java)
                                    intent.putExtra(MapUserDetailActivity.USER, user)
                                    context.startActivity(intent)
                                }
                            }
                        }

                }

                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.KOREA)
                sdf.timeZone = TimeZone.getTimeZone("Asia/Seoul")
                val formattedDate = sdf.format(Date(reCommentData.timestamp))
                tvLastTime.text = formattedDate
                tvDetailContents.text = reCommentData.content

                threedots.setOnClickListener {
                    modifyClick(reCommentData)
                }
                btThumbsUp.setOnClickListener {
                    likeClick.invoke(reCommentData, ivThumbsUp, textView11, textView9)
                }

            }
        }
    }

    object ReCommentDiffCallback : DiffUtil.ItemCallback<ReCommentData>() {
        override fun areItemsTheSame(oldItem: ReCommentData, newItem: ReCommentData): Boolean {
            return oldItem.reCommentId == newItem.reCommentId
        }

        override fun areContentsTheSame(oldItem: ReCommentData, newItem: ReCommentData): Boolean {
            return oldItem == newItem
        }
    }
}
