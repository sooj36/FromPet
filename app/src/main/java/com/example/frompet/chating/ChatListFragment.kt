package com.example.frompet.chating

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.frompet.chating.adapter.ChatListAdapter
import com.example.frompet.databinding.FragmentChatListBinding
import com.example.frompet.login.viewmodel.UserViewModel


class ChatListFragment : Fragment() {
    private var _binding: FragmentChatListBinding? = null
    private val binding get() = _binding!!
    private val adapter: ChatListAdapter by lazy { binding.rvChatList.adapter as ChatListAdapter }
    private val viewModel: UserViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
            rvChatList.adapter = ChatListAdapter(requireContext())
            rvChatList.layoutManager = GridLayoutManager(context, 2)


        viewModel.matchedList.observe(viewLifecycleOwner) { matchedUsers ->
            Log.d("jun", "매치리스트옵저버: ${matchedUsers?.size}")
            (rvChatList.adapter as ChatListAdapter).submitList(matchedUsers)
        }

        viewModel.likeList.observe(viewLifecycleOwner) { users ->
            Log.d("jun", "라이크리스트 옵저버: ${users?.size}")
            (rvChatList.adapter as ChatListAdapter).submitList(users)
        }

        viewModel.loadMatchedUsers()
        viewModel.loadlikes()
    }}



    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
