package com.pet.frompet.ui.chat.fragment

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.Pair
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.pet.frompet.MatchSharedViewModel
import com.pet.frompet.R
import com.pet.frompet.ui.chat.adapter.ChatListAdapter
import com.pet.frompet.databinding.FragmentChatListBinding
import com.pet.frompet.ui.chat.activity.ChatUserDetailActivity
import com.pet.frompet.ui.home.HomeDetailPage


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
    private val matchSharedViewModel: MatchSharedViewModel by activityViewModels()

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
            rvChatList.adapter = ChatListAdapter(requireContext()) { user, imageView ->
                val intent = Intent(requireActivity(), ChatUserDetailActivity::class.java)
                intent.putExtra(USER, user)

                val options: ActivityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    requireActivity(),
                    androidx.core.util.Pair(imageView,"imageTransition2"))

                startChatDetailActivity.launch(intent, options)
            }
            rvChatList.layoutManager = GridLayoutManager(context, 2)

            matchSharedViewModel.likeList.observe(viewLifecycleOwner) { users ->
                users?.let {
                    (rvChatList.adapter as ChatListAdapter).submitList(it)
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
