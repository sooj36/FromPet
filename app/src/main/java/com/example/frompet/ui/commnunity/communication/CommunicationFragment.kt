package com.example.frompet.ui.commnunity.communication

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.example.frompet.R
import com.example.frompet.data.model.CommunicationData
import com.example.frompet.databinding.FragmentCommunicationBinding
import com.example.frompet.ui.commnunity.community.CommunityActivity


class CommunicationFragment : Fragment() {

    private var _binding: FragmentCommunicationBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter : CommunicationAdapter
    private lateinit var communicationData : MutableList<CommunicationData>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
         _binding = FragmentCommunicationBinding.inflate(inflater,container,false)


        val recyclerView = binding.communicationrecyclerView
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 4)

        // data list
        communicationData = mutableListOf(
            CommunicationData(R.drawable.dog, "강아지"),
            CommunicationData(R.drawable.cat, "고양이"),
            CommunicationData(R.drawable.raccoon, "라쿤"),
            CommunicationData(R.drawable.fox, "여우"),
            CommunicationData(R.drawable.chick, "새"),
            CommunicationData(R.drawable.pig, "돼지"),
            CommunicationData(R.drawable.snake, "파충류"),
            CommunicationData(R.drawable.fish, "물고기"),
        )

        //adapter
        val adapter = CommunicationAdapter(communicationData)
        recyclerView.adapter = adapter
        adapter.submitList(communicationData)



        return binding.root
    }




    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}