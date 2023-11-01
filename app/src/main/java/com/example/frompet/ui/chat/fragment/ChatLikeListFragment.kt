package com.example.frompet.ui.chat.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.frompet.MatchSharedViewModel
import com.example.frompet.R
import com.example.frompet.ui.chat.adapter.ChatListAdapter
import com.example.frompet.databinding.FragmentChatListBinding
import com.example.frompet.ui.chat.activity.ChatUserDetailActivity


class ChatLikeListFragment : Fragment() {
    companion object{
        const val MATCHED_USERS = "matchedUser"
        const val USER = "user"
        const val ACTION = "action"
        const val MATCH = "match"
        const val DISLIKE = "dislike"
    }
    private var _binding: FragmentChatListBinding? = null
    private val binding get() = _binding!!
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
        return binding.root
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

                    val text = "${it.size}마리가 나를 좋아해요"
                    val spannable = SpannableStringBuilder(text)
                    val colorSpan = ForegroundColorSpan(ContextCompat.getColor(context?:return@let, R.color.lip_pink))
                    spannable.setSpan(colorSpan, 0, "${it.size}".length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

                    binding.tvLikeMe?.text = spannable
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
