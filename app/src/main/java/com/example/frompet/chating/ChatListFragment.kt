package com.example.frompet.chating

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.frompet.chating.adapter.ChatListAdapter
import com.example.frompet.databinding.FragmentChatListBinding
import com.example.frompet.login.viewmodel.UserViewModel


class ChatListFragment : Fragment() {
    private var _binding: FragmentChatListBinding? = null
    private val binding get() = _binding!!
    private val adapter: ChatListAdapter by lazy { binding.rvChatList.adapter as ChatListAdapter }
    private val viewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChatListBinding.inflate(inflater, container, false)

        binding.apply {
            val chatListAdapter = ChatListAdapter(requireContext())
            rvChatList.adapter = chatListAdapter
            rvChatList.layoutManager = GridLayoutManager(context, 2)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.loadlikes()
        viewModel.likeList.observe(viewLifecycleOwner) { users ->
            users?.let {
                adapter.submitList(it)
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
