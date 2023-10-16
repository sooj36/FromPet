package com.example.frompet.chating

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.frompet.R
import com.example.frompet.chating.adapter.ChatListAdapter
import com.example.frompet.databinding.FragmentChatListBinding
import com.example.frompet.login.viewmodel.MatchViewModel


class ChatListFragment : Fragment() {
    companion object{
        const val MATCHED_USERS = "matchedUser"
        const val USER = "user"
        const val ACTION = "action"
        const val MATCH = "match"
        const val DISLIKE = "dislike"
    }
    private var _binding: FragmentChatListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MatchViewModel by viewModels()

    private val startChatDetailActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val action = result.data?.getStringExtra(ACTION)
            val matchedUserId = result.data?.getStringExtra(MATCHED_USERS)
            matchedUserId?.let { userId ->
                when (action) {
                    MATCH -> viewModel.matchWithUser(userId)
                    DISLIKE -> {val currentLikes = viewModel.likeList.value?.toMutableList() ?: mutableListOf()
                        currentLikes.removeIf { it.uid == userId }
                        viewModel.likeList.value = currentLikes}
                }
            }
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChatListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            rvChatList.adapter = ChatListAdapter(requireContext()) { user ->
                val intent = Intent(context, ChatUserDetailActivity::class.java)
                intent.putExtra(USER, user)
                startChatDetailActivity.launch(intent)
            }
            rvChatList.layoutManager = GridLayoutManager(context, 2)

            viewModel.likeList.observe(viewLifecycleOwner) { users ->
                users?.let {
                    (rvChatList.adapter as ChatListAdapter).submitList(it)
                    binding.tvLikeMe.text = getString(R.string.like_me_text ,it.size)
                }
            }
            viewModel.loadlikes()

        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}

