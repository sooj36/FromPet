package com.example.frompet.ui.commnunity.communication

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.frompet.data.model.CommunicationData
import com.example.frompet.databinding.ItemCoummunicationBinding
import com.example.frompet.ui.chat.activity.ChatPullScreenActivity
import com.example.frompet.ui.chat.adapter.ChatMessageAdapter

class CommunicationAdapter(communicationFragment: List<CommunicationData>) :
    ListAdapter<CommunicationData, CommunicationAdapter.CommunicationViewHolder>(
        object : DiffUtil.ItemCallback<CommunicationData>() {
            override fun areItemsTheSame(
                oldItem: CommunicationData,
                newItem: CommunicationData
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: CommunicationData,
                newItem: CommunicationData
            ): Boolean {
                return oldItem == newItem
            }
        }
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommunicationViewHolder {
        val binding = ItemCoummunicationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CommunicationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CommunicationViewHolder, position : Int) {
        // currentList : 해당 Adapter 에 " submitList()를 통해 삽입한 아이템 리스트
//        holder.bind(currentList[position])
    }

    private fun clickListener(imageButton: ImageButton, imageUrl: String?) {
        imageButton.setOnClickListener {
            val intent = Intent(it.context, ChatPullScreenActivity::class.java)
            intent.putExtra(ChatMessageAdapter.IMAGE_URL, imageUrl)
            it.context.startActivity(intent)
        }
    }


    class CommunicationViewHolder(private val binding: ItemCoummunicationBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(communicationData: CommunicationData) {
            binding.ivPetNameComm.load(communicationData.pet_logo)
            binding.tvPetNameComm.text = communicationData.pet_name
        }
    }
}