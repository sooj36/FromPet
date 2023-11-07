package com.example.frompet.ui.commnunity.community

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.frompet.data.model.CommunityData
import com.example.frompet.databinding.ItemCommunityBinding
import com.example.frompet.util.FormatDate
import com.example.frompet.util.ViewCountManager
import com.example.frompet.util.getAddressGeocoder
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CommunityAdapter(private val ListClick: (CommunityData) -> Unit) :
    ListAdapter<CommunityData, CommunityAdapter.CommunityViewHolder>(DiffCallback()) {


    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): CommunityAdapter.CommunityViewHolder {
        return CommunityViewHolder(
            ItemCommunityBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            ), ListClick
        )
    }

    override fun onBindViewHolder(holder: CommunityAdapter.CommunityViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }


    inner class CommunityViewHolder(
        private val binding: ItemCommunityBinding,
        private val ListClick: (CommunityData) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {
        private val viewCountManager = ViewCountManager()
        fun bind(communityData: CommunityData) {
            binding.root.setOnClickListener {
                ListClick(communityData)

                viewCountManager.checkedViewCount(FirebaseFirestore.getInstance(), communityData.docsId) {
                    Log.d("view","${communityData.docsId}")
                    communityData.viewCount = communityData.viewCount?.plus(1)
                    binding.tvViewCount.text = communityData.viewCount.toString()
                }
            }

            binding.tvTitleComm.text = communityData.title
            binding.chipTag.text = communityData.tag
            binding.tvContentsComm.text = communityData.contents
            fetchUserLocation(binding.root.context, communityData.uid) { latitude, longitude ->
                CoroutineScope(Dispatchers.Main).launch {
                    val address = getAddressGeocoder(binding.root.context, latitude, longitude)
                    binding.tvAddress.text = address
                }
            }
            binding.tvLastTime.text =FormatDate.formatDate(communityData.timestamp)
            binding.tvViewCount.text = communityData.viewCount.toString()


//            binding.linearlayoutAdd.setOnClickListener {
//                ListClick(communityData)
//            }

        }
    }

    class DiffCallback : DiffUtil.ItemCallback<CommunityData>() {
        override fun areItemsTheSame(oldItem: CommunityData, newItem: CommunityData): Boolean {
            return oldItem.title == newItem.title
        }

        override fun areContentsTheSame(oldItem: CommunityData, newItem: CommunityData): Boolean {
            return oldItem == newItem
        }
    }
    fun fetchUserLocation(context: Context, uid: String, onLocationFetched: (Double, Double) -> Unit) {
        val locationRef = FirebaseDatabase.getInstance().getReference("location/$uid")
        locationRef.get().addOnSuccessListener { dataSnapshot ->
            val latitude = dataSnapshot.child("latitude").getValue(Double::class.java) ?: 0.0
            val longitude = dataSnapshot.child("longitude").getValue(Double::class.java) ?: 0.0
            onLocationFetched(latitude, longitude)
        }.addOnFailureListener {
        }
    }
}