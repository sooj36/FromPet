package com.example.frompet.ui.chat

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.frompet.MatchSharedViewModel
import com.example.frompet.ui.chat.adapter.ChatListAdapter
import com.example.frompet.databinding.FragmentChatListBinding


class ChatLikeListFragment : Fragment() {
    companion object{
        const val MATCHED_USERS = "matchedUser"
        const val USER = "user"
        const val ACTION = "action"
        const val MATCH = "match"
        const val DISLIKE = "dislike"
    }
    private var _binding: FragmentChatListBinding? = null
    private val binding get() = _binding
    private val matchSharedViewModel: MatchSharedViewModel by viewModels()

    private val startChatDetailActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val action = result.data?.getStringExtra(ACTION)
            val matchedUserId = result.data?.getStringExtra(MATCHED_USERS)
            matchedUserId?.let { userId ->
                when (action) {
                    MATCH -> matchSharedViewModel.matchUser(userId)
                    DISLIKE -> {val currentLikes = matchSharedViewModel.likeList.value?.toMutableList() ?: mutableListOf()
                        currentLikes.removeIf { it.uid == userId }
                        matchSharedViewModel.likeList.value = currentLikes}
                }
            }
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChatListBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding?.apply {
            rvChatList.adapter = ChatListAdapter(requireContext()) { user ->
                val intent = Intent(context, ChatUserDetailActivity::class.java)
                intent.putExtra(USER, user)
                startChatDetailActivity.launch(intent)
            }
            rvChatList.layoutManager = GridLayoutManager(context, 2)

            matchSharedViewModel.likeList.observe(viewLifecycleOwner) { users ->
                users?.let {
                    (rvChatList.adapter as ChatListAdapter).submitList(it)
                    binding?.tvLikeMe?.text = "${it.size}명이 나를 좋아해요"
                }
            }
            matchSharedViewModel.loadlike()

        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}

