package com.example.frompet.ui.commnunity.communityhome

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.example.frompet.R
import com.example.frompet.data.model.CommunityHomeData
import com.example.frompet.databinding.FragmentCommunityhomeBinding


class CommunityHomeFragment : Fragment() {

    private var _binding: FragmentCommunityhomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter : CommunityHomeAdapter
    private lateinit var communityHomeData : MutableList<CommunityHomeData>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
         _binding = FragmentCommunityhomeBinding.inflate(inflater,container,false)


        val recyclerView = binding.communicationrecyclerView
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 4)

        // data list
        communityHomeData = mutableListOf(
            CommunityHomeData(R.drawable.dog, "DOG"),
            CommunityHomeData(R.drawable.cat, "CAT"),
            CommunityHomeData(R.drawable.raccoon, "라쿤"),
            CommunityHomeData(R.drawable.fox, "여우"),
            CommunityHomeData(R.drawable.chick, "새"),
            CommunityHomeData(R.drawable.pig, "돼지"),
            CommunityHomeData(R.drawable.snake, "파충류"),
            CommunityHomeData(R.drawable.fish, "물고기"),
        )

        //adapter
        val adapter = CommunityHomeAdapter(communityHomeData)
        recyclerView.adapter = adapter
        adapter.submitList(communityHomeData)



        return binding.root
    }




    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}